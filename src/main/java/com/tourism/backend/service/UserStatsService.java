package com.tourism.backend.service;

import com.tourism.backend.dto.response.UserStatsResponseDTO;

public interface UserStatsService {
    UserStatsResponseDTO getUserStats(String username);
}
