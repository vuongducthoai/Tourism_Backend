package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageDTO {
    private Integer imageId;
    private String imageUrl;
    private String caption;
    private String altText;
    private Integer displayOrder;
}