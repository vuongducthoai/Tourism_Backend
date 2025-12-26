package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponseDTO {
    private Integer totalPosts;
    private Integer totalComments;
    private Integer totalLikesReceived;
    private Integer reputationPoints;
}
