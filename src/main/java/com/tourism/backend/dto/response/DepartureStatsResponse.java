package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureStatsResponse {

    private Long totalDepartures;

    private Long activeDepartures;

    private Long inactiveDepartures;

    private Long upcomingDepartures;

    private Long pastDepartures;

    private Integer totalAvailableSlots;

    private Integer totalBookedSlots;

    private BigDecimal totalRevenue;

    private Double averageBookingRate;
}
