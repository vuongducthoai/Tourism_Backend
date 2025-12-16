package com.tourism.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourMediaResponse {
    private Integer mediaId;
    private String mediaUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Double duration;
    private Long fileSize;
    private Boolean isPrimary;
}
