package com.tourism.backend.service;

import com.tourism.backend.dto.request.RegisterRequestDTO;
import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.response.RegisterResponseDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import org.apache.coyote.BadRequestException;

public interface UserService {
    UserReaponseDTO getUserById(Integer userID);
    UserReaponseDTO updateUser(Integer userID, UserUpdateRequestDTO updateDTO);
    RegisterResponseDTO register(RegisterRequestDTO requestDTO) throws BadRequestException;
}

