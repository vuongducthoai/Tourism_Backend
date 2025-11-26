package com.tourism.backend.dto.response;

import com.tourism.backend.enums.TransportType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DepartureDTO {
    private Integer departureId;
    private Integer availableSlots;
    private List<DepartureTransportDTO> departureTransportDTOS;
}
