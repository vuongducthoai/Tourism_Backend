package com.tourism.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tourism.backend.enums.TransportType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureTransportResponse {
    private Integer transportID;

    private TransportType type;

    private String transportCode;

    private String vehicleName;

    private String startPoint;

    private String endPoint;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;
}
