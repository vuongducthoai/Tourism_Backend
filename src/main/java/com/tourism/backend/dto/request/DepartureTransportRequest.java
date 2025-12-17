package com.tourism.backend.dto.request;
import com.tourism.backend.enums.TransportType;
import com.tourism.backend.enums.VehicleTyle;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureTransportRequest {
    @NotNull(message = "Loại phương tiện không được để trống")
    private TransportType type;

    private String transportCode;

    private VehicleTyle vehicleType;

    private String vehicleName;

    private String startPoint;

    private String endPoint;

    @NotNull(message = "Thời gian khởi hành không được để trống")
    private LocalDateTime departTime;

    @NotNull(message = "Thời gian đến không được để trống")
    private LocalDateTime arrivalTime;

}
