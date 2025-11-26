package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransportDTO {
    private String type;
    private String transportCode;
    private String vehicleName;
    private String startPoint;
    private String endPoint;
    private LocalDateTime departTime;
    private LocalDateTime arrivalTime;
}
