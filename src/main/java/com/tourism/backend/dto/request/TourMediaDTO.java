package com.tourism.backend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourMediaDTO {
    private Integer mediaId;
    private String mediaUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Double duration;
    private Long fileSize;
    private Boolean isPrimary;
}
