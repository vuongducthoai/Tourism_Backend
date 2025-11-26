package com.tourism.backend.dto.response;

import com.tourism.backend.enums.TransportType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DepartureTransportDTO {
    private Integer transportId;
    private TransportType transportType;
    private String transportCode;
    private LocalDate departureTime;
    private LocalDate arrivalTime;
}
