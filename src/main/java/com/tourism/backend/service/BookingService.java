package com.tourism.backend.service;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;

public interface BookingService {
    TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId);
    BookingDetailResponseDTO createBooking(BookingRequestDTO request);
    BookingDetailResponseDTO getBookingDetail(String bookingCode);
}
