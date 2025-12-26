package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageResponseDTO {
    private Integer imageId;
    private String imageUrl;
    private String publicId;
    private Long fileSize;
    private String mimeType;
}