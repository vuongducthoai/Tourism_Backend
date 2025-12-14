// src/main/java/com/tourism/backend/service/GeminiAIService.java
package com.tourism.backend.service;

import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;
import java.util.List;

public interface GeminiAIService {
    String generateDashboardSummary(String context);
    List<DashboardStatsDTO.Insight> generateInsights(String context);
    List<DashboardStatsDTO.Prediction> generatePredictions(String context);
    List<DashboardStatsDTO.Recommendation> generateRecommendations(String context);
}