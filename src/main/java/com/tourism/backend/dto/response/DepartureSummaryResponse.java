package com.tourism.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureSummaryResponse {
    private Integer departureID;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureDate;
    private Boolean status;
    private Integer availableSlots;
    private Integer bookedSlots;
    private String tourCode;
    private String tourName;
    private String tourDuration;
    private BigDecimal lowestPrice;
    private Integer totalBookings;
    private Boolean hasOutboundTransport;  
    private Boolean hasInboundTransport;
}
