package com.tourism.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloneDepartureRequest {
    @NotNull(message = "Ngày khởi hành mới không được để trống")
    @Future(message = "Ngày khởi hành phải là ngày trong tương lai")
    private LocalDate newDepartureDate;

    private Boolean copyPricing;

    private Boolean copyTransport;
}
