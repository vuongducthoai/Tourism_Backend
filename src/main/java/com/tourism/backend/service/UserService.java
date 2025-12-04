package com.tourism.backend.service;

import com.tourism.backend.dto.request.RegisterRequestDTO;
import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.response.RegisterResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import org.apache.coyote.BadRequestException;

import java.io.IOException;

public interface UserService {
    UserReaponseDTO getUserById(Integer userID);
    UserReaponseDTO updateUser(Integer userID, UserUpdateRequestDTO updateDTO);
    RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws BadRequestException;
    String verifyEmail(String token) throws com.tourism.backend.exception.BadRequestException;
    void resendVerificationEmail(String email);
    UserReaponseDTO updateUser(Integer userID, UserUpdateRequestDTO updateDTO) throws IOException;
}

