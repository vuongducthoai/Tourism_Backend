package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItineraryDTO {
    private Integer dayNumber;
    private String title;
    private String meals;
    private String description;
}
