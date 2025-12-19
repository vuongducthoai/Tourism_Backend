package com.tourism.backend.controller;

import com.tourism.backend.dto.request.ChangePasswordRequest;
import com.tourism.backend.dto.request.UpdateProfileRequest;
import com.tourism.backend.dto.response.UserResponseDTO;
import com.tourism.backend.entity.User;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.exception.UnauthorizedException;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
@Slf4j
public class AdminProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("User không tồn tại"));

            // Update fields
            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth());
            }
            if (request.getProvinceName() != null) {
                user.setProvinceName(request.getProvinceName());
            }
            if (request.getProvinceCode() != null) {
                user.setProvinceCode(request.getProvinceCode());
            }
            if (request.getDistrictName() != null) {
                user.setDistrictName(request.getDistrictName());
            }
            if (request.getDistrictCode() != null) {
                user.setDistrictCode(request.getDistrictCode());
            }
            if (request.getAvatar() != null) {
                user.setAvatar(request.getAvatar());
            }

            userRepository.save(user);

            log.info("Profile updated successfully for user: {}", email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật thông tin thành công",
                    "data", new UserResponseDTO(user)
            ));

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra"));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("User không tồn tại"));

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Mật khẩu hiện tại không đúng");
            }

            if (request.getNewPassword().length() < 6) {
                throw new BadRequestException("Mật khẩu mới phải có ít nhất 6 ký tự");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            log.info("Password changed successfully for user: {}", email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đổi mật khẩu thành công"
            ));

        } catch (BadRequestException | UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra"));
        }
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("File không được để trống");
            }

            if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                throw new BadRequestException("Kích thước file không được vượt quá 5MB");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException("File phải là ảnh");
            }

            String avatarUrl = "https://example.com/avatars/" + file.getOriginalFilename();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "url", avatarUrl
            ));

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading avatar", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Có lỗi xảy ra khi upload ảnh"));
        }
    }
}