package com.tourism.backend.controller;

import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;
import com.tourism.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatsDTO> getDashboardStatistics() {
        DashboardStatsDTO stats = dashboardService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/analysis")
    public ResponseEntity<DashboardStatsDTO.AIAnalysis> getDashboardAIAnalysis() {
        DashboardStatsDTO.AIAnalysis analysis = dashboardService.getDashboardAIAnalysis();
        return ResponseEntity.ok(analysis);
    }
}