package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourBookingInfoDTO {
    private Integer tourId;
    private String tourName;
    private String tourCode;
    private String image;
    private Integer durationDays;
    private Integer availableSlots;

    private BigDecimal adultPrice;
    private BigDecimal childPrice;
    private BigDecimal toddlerPrice;
    private BigDecimal infantPrice;
    private BigDecimal singleRoomSurcharge;

    private BookingFlightDTO outboundFlight;
    private BookingFlightDTO inboundFlight;

    private CouponDTO departureCoupon;
    private List<CouponDTO> globalCoupons;
}
