package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.UserReaponseDTO;
import com.tourism.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final ModelMapper modelMapper;

    /**
     * Chuyển đổi Entity User sang UserResponseDTO
     */
    public UserReaponseDTO convertToUserResponseDTO(User user) {
        return modelMapper.map(user, UserReaponseDTO.class);
    }
}


