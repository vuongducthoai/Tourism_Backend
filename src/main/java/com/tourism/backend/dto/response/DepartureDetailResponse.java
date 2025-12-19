package com.tourism.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureDetailResponse {
    private Integer departureID;
    private LocalDateTime departureDate;
    private Boolean status;
    private Integer availableSlots;
    private String tourGuideInfo;

    private Integer tourId;
    private String tourCode;
    private String tourName;

    private Integer policyTemplateId;
    private String policyTemplateName;

    private Integer couponId;
    private String couponCode;

    private List<DeparturePricingResponse> pricings;
    private DepartureTransportResponse outboundTransport;
    private DepartureTransportResponse inboundTransport;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
