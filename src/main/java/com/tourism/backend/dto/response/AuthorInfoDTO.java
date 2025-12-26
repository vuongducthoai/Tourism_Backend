package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorInfoDTO {
    private Integer userId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private Integer totalPosts;
    private Integer totalFollowers;
    private Integer reputationPoints;
}
