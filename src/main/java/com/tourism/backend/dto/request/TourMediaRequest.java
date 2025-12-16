package com.tourism.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourMediaRequest {
    private String mediaUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Double duration;
    private Long fileSize;
    private Boolean isPrimary;
}
