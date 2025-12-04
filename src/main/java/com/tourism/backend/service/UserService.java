package com.tourism.backend.service;

import com.tourism.backend.dto.requestDTO.UserUpdateRequestDTO;
import com.tourism.backend.dto.responseDTO.UserReaponseDTO;

import java.io.IOException;

public interface UserService {
    UserReaponseDTO getUserById(Integer userID);
    UserReaponseDTO updateUser(Integer userID, UserUpdateRequestDTO updateDTO) throws IOException;
}

