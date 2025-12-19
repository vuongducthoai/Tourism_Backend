package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DepartureSimpleResponse {
    private Integer departureID;
    private LocalDateTime departureDate;
    private Integer availableSlots;
}