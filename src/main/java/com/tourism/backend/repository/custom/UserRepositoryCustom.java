package com.tourism.backend.repository.custom;

import com.tourism.backend.dto.requestDTO.UserSearchRequestDTO;
import com.tourism.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> searchUsers(UserSearchRequestDTO searchDTO, Pageable pageable);
}