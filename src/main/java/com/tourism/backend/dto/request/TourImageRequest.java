package com.tourism.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourImageRequest {
    private String imageURL;
    private Boolean isMainImage;
}
