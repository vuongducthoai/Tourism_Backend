// src/main/java/com/tourism/backend/service/DashboardService.java
package com.tourism.backend.service;

import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;

public interface DashboardService {
    DashboardStatsDTO getDashboardStatistics();
    DashboardStatsDTO.AIAnalysis getDashboardAIAnalysis();
}