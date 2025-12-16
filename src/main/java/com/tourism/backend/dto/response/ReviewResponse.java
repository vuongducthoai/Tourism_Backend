package com.tourism.backend.dto.response;

import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer reviewId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    private UserInfo user;

    private List<String> images;

    private String tourCode;
    private String tourName;
    private String departureName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Integer userId;
        private String fullName;
        private String avatar;
        private String email;
    }
}
