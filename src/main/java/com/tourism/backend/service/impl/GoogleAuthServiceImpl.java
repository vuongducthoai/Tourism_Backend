package com.tourism.backend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tourism.backend.dto.request.GoogleLoginRequest;
import com.tourism.backend.dto.response.GoogleUserInfo;
import com.tourism.backend.dto.response.LoginResponse;
import com.tourism.backend.entity.RefreshToken;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.Role;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.repository.RefreshTokenRepository;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.security.JwtUtil;
import com.tourism.backend.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Override
    public LoginResponse loginWithGoogle(GoogleLoginRequest request, HttpServletRequest httpRequest) {
        // 1. Verify Google ID Token
        GoogleUserInfo googleUserInfo = verifyGoogleToken(request.getIdToken());

        // 2. Tìm hoặc tạo user
        User user = findOrCreateUser(googleUserInfo);

        // 3. Tạo JWT tokens
        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getUserID(),
                user.getRole().name()
        );

        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = createRefreshToken(user, refreshTokenValue, httpRequest);
        refreshTokenRepository.save(refreshToken);

        log.info("User logged in with Google: {}", user.getEmail());

        // 4. Trả về response
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
    public GoogleUserInfo verifyGoogleToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BadRequestException("Invalid Google ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            return GoogleUserInfo.builder()
                    .id(payload.getSubject())
                    .email(payload.getEmail())
                    .name((String) payload.get("name"))
                    .givenName((String) payload.get("given_name"))
                    .familyName((String) payload.get("family_name"))
                    .picture((String) payload.get("picture"))
                    .verifiedEmail(payload.getEmailVerified())
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to verify Google ID Token", e);
            throw new BadRequestException("Could not verify Google token");
        }
    }

    @Override
    public User findOrCreateUser(GoogleUserInfo googleUserInfo) {
        return userRepository.findByEmail(googleUserInfo.getEmail())
                .orElseGet(() -> {
                    log.info("Creating new user from Google: {}", googleUserInfo.getEmail());

                    User newUser = User.builder()
                            .fullName(googleUserInfo.getName())
                            .email(googleUserInfo.getEmail())
                            .password(UUID.randomUUID().toString())
                            .role(Role.CUSTOMER)
                            .status(true)
                            .isEmailVerified(true)
                            .coinBalance(BigDecimal.valueOf(0))
                            .build();

                    return userRepository.save(newUser);
                });
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
}
