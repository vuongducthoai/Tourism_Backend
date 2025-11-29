package com.tourism.backend.service;

import com.tourism.backend.dto.response.TourBookingInfoDTO;

public interface BookingService {
    TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId);
}
