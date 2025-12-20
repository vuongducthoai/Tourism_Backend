package com.tourism.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartureRequest {
    private LocalDateTime departureDate;

    @Min(value = 1, message = "Số chỗ trống phải >= 1")
    private Integer availableSlots;

    private String tourGuideInfo;

    private Integer policyTemplateId;

    private Integer couponId;

    private Boolean status;

    @Valid
    private List<DeparturePricingRequest> pricings;

    @Valid
    private DepartureTransportRequest outboundTransport;

    @Valid
    private DepartureTransportRequest inboundTransport;
}
