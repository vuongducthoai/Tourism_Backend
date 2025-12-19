package com.tourism.backend.controller;

import com.tourism.backend.dto.request.LoginRequest;
import com.tourism.backend.dto.request.RefreshTokenRequest;
import com.tourism.backend.dto.response.LoginResponse;
import com.tourism.backend.dto.response.TokenResponse;
import com.tourism.backend.dto.response.UserResponseDTO;
import com.tourism.backend.enums.Role;
import com.tourism.backend.exception.UnauthorizedException;
import com.tourism.backend.service.AuthService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * Admin login endpoint
     * Only allows users with ADMIN or STAFF role
     */
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        try {
            log.info("Admin login attempt for email: {}", request.getEmail());

            // Authenticate user
            LoginResponse response = authService.login(request, httpRequest);

            // Verify user has admin privileges
            if (response.getUser() != null) {
                String userRole = response.getUser().getRole();

                if (!"ADMIN".equals(userRole) && !"STAFF".equals(userRole)) {
                    log.warn("Non-admin user attempted to login to admin portal: {}", request.getEmail());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "success", false,
                                    "message", "Bạn không có quyền truy cập vào hệ thống quản trị"
                            ));
                }
            }

            log.info("Admin login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);

        } catch (UnauthorizedException e) {
            log.error("Admin login failed - Unauthorized: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Admin login error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Email hoặc mật khẩu không chính xác"
                    ));
        }
    }

    /**
     * Get admin profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Chưa đăng nhập"));
            }

            String email = authentication.getName();
            UserResponseDTO userProfile = userService.getUserProfile(email);

            // Verify admin role
            if (userProfile.getRole() != Role.ADMIN ) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "Không có quyền truy cập"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", userProfile
            ));

        } catch (Exception e) {
            log.error("Get admin profile error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra"));
        }
    }

    /**
     * Refresh admin token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAdminToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse response = authService.refreshToken(request);

            return ResponseEntity.ok(response);

        } catch (UnauthorizedException e) {
            log.error("Admin refresh token error - Unauthorized: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Admin refresh token error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Token không hợp lệ hoặc đã hết hạn"
                    ));
        }
    }

    /**
     * Admin logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> adminLogout(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Refresh token không được để trống"
                        ));
            }

            authService.logout(refreshToken);

            log.info("Admin logout successful");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đăng xuất thành công"
            ));

        } catch (Exception e) {
            log.error("Admin logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Có lỗi xảy ra khi đăng xuất"
                    ));
        }
    }

    /**
     * Admin logout from all devices
     */
    @PostMapping("/logout-all")
    public ResponseEntity<?> adminLogoutAll(@RequestBody Map<String, Integer> request) {
        try {
            Integer userId = request.get("userId");

            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "User ID không được để trống"
                        ));
            }

            authService.logoutAll(userId);

            log.info("Admin logout all devices successful for userId: {}", userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã đăng xuất khỏi tất cả thiết bị"
            ));

        } catch (UnauthorizedException e) {
            log.error("Admin logout all error - Unauthorized: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Admin logout all error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Có lỗi xảy ra khi đăng xuất"
                    ));
        }
    }
}