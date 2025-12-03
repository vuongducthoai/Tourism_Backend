package com.tourism.backend.service.impl;

import com.tourism.backend.convert.UserConverter;
import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import com.tourism.backend.entity.User;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
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
}

