package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.LoginRequest;
import com.tourism.backend.dto.request.RefreshTokenRequest;
import com.tourism.backend.dto.response.LoginResponse;
import com.tourism.backend.dto.response.TokenResponse;
import com.tourism.backend.entity.RefreshToken;
import com.tourism.backend.entity.User;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.exception.UnauthorizedException;
import com.tourism.backend.repository.RefreshTokenRepository;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.security.JwtUtil;
import com.tourism.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không đúng"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new UnauthorizedException("Email hoặc mật khẩu không đúng");
        }

        if(!user.getStatus()){
            throw new BadRequestException("Tài khoản đã bị khóa");
        }

        if(!user.getIsEmailVerified()){
            throw new BadRequestException("Vui lòng xác thực email trước khi đăng nhập");
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getUserID(),
                user.getRole().name()
        );

        // 6. Tạo refresh token
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = createRefreshToken(user, refreshTokenValue, httpRequest);

        // 7. Lưu refresh token vào database
        refreshTokenRepository.save(refreshToken);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .user(LoginResponse.UserInfo.builder()
                        .userId(user.getUserID())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .provinceName(user.getProvinceName())
                        .districtName(user.getDistrictName())
                        .build())
                .build();
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        //Tìm refresh token trong database
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token không hợp lệ"));

        //Kiểm tra token đã hết hạn chưa
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token đã hết hạn");
        }

        //Kiểm tra token đã bị thu hồi chưa
        if (refreshToken.getRevoked()) {
            throw new UnauthorizedException("Refresh token đã bị thu hồi");
        }

        User user = refreshToken.getUser();
        String newRefreshTokenValue = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenValue)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        log.info("Token refreshed for user: {}", user.getEmail());


      //  Tạo access token mới
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getUserID(),
                user.getRole().name()
        );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .build();

    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("User logged out: {}", token.getUser().getEmail());
                });
    }

    @Override
    public void logoutAll(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User không tồn tại"));

        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("All tokens revoked for user: {}", user.getEmail());
    }

    @Override
    public RefreshToken createRefreshToken(User user, String token, HttpServletRequest request) {
        return RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();
    }

    @Override
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired tokens cleaned");
    }
}
