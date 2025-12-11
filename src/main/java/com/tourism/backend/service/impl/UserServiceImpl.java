package com.tourism.backend.service.impl;

import com.tourism.backend.convert.UserConverter;
import com.tourism.backend.dto.request.RegisterRequestDTO;
import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.response.RegisterResponseDTO;
import com.tourism.backend.dto.response.UserResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.Role;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.EmailService;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final CloudinaryService cloudinaryService;
    @Override
    @Transactional(readOnly = true)
    public UserReaponseDTO getUserById(Integer userID) {
        User user = userRepository.findByUserID(userID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userID));
        
        return userConverter.convertToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserReaponseDTO updateUser(Integer userID, UserUpdateRequestDTO updateDTO)throws IOException {
        User user = userRepository.findByUserID(userID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userID));
        MultipartFile avatarFile = updateDTO.getAvatar();
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Tên thư mục trên Cloudinary: avatars (Theo yêu cầu)
            String avatarUrl = cloudinaryService.uploadImage(avatarFile, "avatars");
            user.setAvatar(avatarUrl);
        }
        // Cập nhật các trường từ DTO
        if (updateDTO.getFullName() != null && !updateDTO.getFullName().isEmpty()) {
            user.setFullName(updateDTO.getFullName());
        }
        
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().isEmpty()) {
            user.setPhone(updateDTO.getPhone());
        }
        
        if (updateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(updateDTO.getDateOfBirth());
        }
        
        // Lưu user đã cập nhật
        User updatedUser = userRepository.save(user);
        
        return userConverter.convertToUserResponseDTO(updatedUser);
    }

    @Override
    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserResponseDTO(user);
    }

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws BadRequestException {
        if(userRepository.existsByEmail(requestDTO.getEmail())){
            throw new BadRequestException("Email đã được đăng ký");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .provinceCode(requestDTO.getProvinceCode())
                .provinceName(requestDTO.getProvinceName())
                .districtCode(requestDTO.getDistrictCode())
                .districtName(requestDTO.getDistrictName())
                .role(Role.CUSTOMER)
                .status(false)
                .isEmailVerified(false)
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusMinutes(5))
                .coinBalance(BigDecimal.valueOf(0))
                .build();

        User saveUser = userRepository.save(user);
        emailService.sendVerificationEmail(saveUser.getEmail(), saveUser.getFullName(), verificationToken);
        log.info("User registered, verification email sent to: {}", saveUser.getEmail());

        return RegisterResponseDTO.builder()
                .userId(saveUser.getUserID())
                .fullName(saveUser.getFullName())
                .email(saveUser.getEmail())
                .provinceName(saveUser.getProvinceName())
                .districtName(saveUser.getDistrictName())
                .message("Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.")
                .build();
    }

    @Transactional
    public String verifyEmail(String token) throws BadRequestException {
        // 1. Tìm user theo token
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new NotFoundException("Token không hợp lệ"));

        // 2. Kiểm tra token đã hết hạn chưa
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token đã hết hạn. Vui lòng đăng ký lại.");
        }

        // 3. Kích hoạt tài khoản
        user.setIsEmailVerified(true);
        user.setStatus(true);  // ✅ Kích hoạt tài khoản
        user.setVerificationToken(null);  // Xóa token sau khi verify
        user.setVerificationTokenExpiry(null);

        userRepository.save(user);

        // 4. Gửi email chào mừng
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());

        log.info("Email verified successfully for: {}", user.getEmail());

        return "Email đã được xác thực thành công!";
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email không tồn tại"));

        if (user.getIsEmailVerified()) {
            throw new BadRequestException("Email đã được xác thực");
        }

        // Tạo token mới
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        // Gửi lại email
        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), newToken);

        log.info("Verification email resent to: {}", email);
    }
}

