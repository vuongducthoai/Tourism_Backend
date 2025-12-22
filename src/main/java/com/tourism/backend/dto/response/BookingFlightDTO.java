package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingFlightDTO {
    private String transportCode;
    private String airlineName;
    private String startPoint;
    private String startPointName;
    private String vehicleType;
    private String vehicleName;
    private String endPoint;
    private String endPointName;
    private LocalDateTime departTime;
    private LocalDateTime arrivalTime;

}
