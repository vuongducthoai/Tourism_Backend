package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FollowerResponseDTO {
    private Integer userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private LocalDateTime followedAt;
}
