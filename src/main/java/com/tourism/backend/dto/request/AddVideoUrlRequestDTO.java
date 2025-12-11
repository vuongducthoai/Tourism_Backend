package com.tourism.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AddVideoUrlRequestDTO {
    @NotBlank(message = "Video URL is required")
    @Pattern(
            regexp = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be|vimeo\\.com)/.+$",
            message = "Must be a valid YouTube or Vimeo URL"
    )
    private String videoUrl;

    private String title;

    private String description;

    private Boolean isPrimary = false;

    private Integer displayOrder;
}
