package com.tourism.backend.controller;

import com.tourism.backend.dto.response.ApiResponse;
import com.tourism.backend.dto.response.UserStatsResponseDTO;
import com.tourism.backend.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/stats")
@RequiredArgsConstructor
public class UserStatsController {
    private final UserStatsService userStatsService;
    @GetMapping
    public ResponseEntity<ApiResponse<UserStatsResponseDTO>> getCurrentUserStats(Authentication authentication) {
        // authentication.getName() thường là username hoặc userID
        String username = authentication.getName(); // hoặc lấy từ token nếu dùng JWT

        UserStatsResponseDTO stats = userStatsService.getUserStats(username);

        return ResponseEntity.ok(ApiResponse.<UserStatsResponseDTO>builder()
                .success(true)
                .message("Lấy thống kê người dùng thành công")
                .data(stats)
                .build());
    }

}
