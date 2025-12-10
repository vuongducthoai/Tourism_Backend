package com.tourism.backend.service;

import com.tourism.backend.dto.request.RegisterRequestDTO;
import com.tourism.backend.dto.requestDTO.UserSearchRequestDTO;
import com.tourism.backend.dto.requestDTO.UserStatusUpdateRequestDTO;
import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.response.RegisterResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface UserService {
    UserReaponseDTO getUserById(Integer userID);
    RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws BadRequestException;
    String verifyEmail(String token) throws com.tourism.backend.exception.BadRequestException;
    void resendVerificationEmail(String email);
    UserReaponseDTO updateUser(Integer userID, UserUpdateRequestDTO updateDTO) throws IOException;
    Page<UserReaponseDTO> searchUsers(UserSearchRequestDTO searchDTO, Pageable pageable);
    UserReaponseDTO updateUserStatus(UserStatusUpdateRequestDTO requestDTO);
}

