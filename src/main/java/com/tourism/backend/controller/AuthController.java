package com.tourism.backend.controller;

import com.tourism.backend.dto.request.GoogleLoginRequest;
import com.tourism.backend.dto.request.LoginRequest;
import com.tourism.backend.dto.request.RefreshTokenRequest;
import com.tourism.backend.dto.request.RegisterRequestDTO;
import com.tourism.backend.dto.response.LoginResponse;
import com.tourism.backend.dto.response.RegisterResponseDTO;
import com.tourism.backend.dto.response.TokenResponse;
import com.tourism.backend.dto.response.UserResponseDTO;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.exception.NotFoundException;
import com.tourism.backend.service.AuthService;
import com.tourism.backend.service.GoogleAuthService;
import com.tourism.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        UserResponseDTO userProfile = userService.getUserProfile(email);

        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO requestDTO) {
        try {
            log.info("Registration request received for email: {}", requestDTO.getEmail());

            RegisterResponseDTO response = userService.register(requestDTO);

            return ResponseEntity.ok(response);

        } catch (BadRequestException e) {
            log.error("Registration failed - Bad Request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Registration error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Đã xảy ra lỗi trong quá trình đăng ký"
                    ));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            log.info("Email verification request received with token: {}", token);

            String message = userService.verifyEmail(token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);

            log.info("Email verification successful for token: {}", token);

            return ResponseEntity.ok(response);

        } catch (NotFoundException e) {
            log.error("Email verification failed - Not Found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Token không tồn tại hoặc không hợp lệ"
                    ));

        } catch (BadRequestException e) {
            log.error("Email verification failed - Bad Request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {
            log.error("Email verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Đã xảy ra lỗi trong quá trình xác thực email"
                    ));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam("email") String email) {
        try {
            log.info("Resend verification request for email: {}", email);

            userService.resendVerificationEmail(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email xác thực đã được gửi lại. Vui lòng kiểm tra hộp thư."
            ));

        } catch (NotFoundException e) {
            log.error("Resend verification failed - Not Found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Email không tồn tại trong hệ thống"
                    ));

        } catch (BadRequestException e) {
            log.error("Resend verification failed - Bad Request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {
            log.error("Resend verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Đã xảy ra lỗi khi gửi lại email xác thực"
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google/login")
    public ResponseEntity<LoginResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletRequest httpRequest) {
        LoginResponse response = googleAuthService.loginWithGoogle(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        authService.logout(refreshToken);
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutAll(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("userId");
        authService.logoutAll(userId);
        return ResponseEntity.ok(Map.of("message", "Đã đăng xuất khỏi tất cả thiết bị"));
    }
}