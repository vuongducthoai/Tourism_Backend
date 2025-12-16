package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class DepartureSimpleResponse {
    private Integer departureID;
    private LocalDate departureDate;
    private Integer availableSlots;
}