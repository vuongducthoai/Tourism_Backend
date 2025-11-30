package com.tourism.backend.service;

import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId);
    List<BookingResponseDTO> getAllBookingsByUser(Integer userID, BookingStatus bookingStatus);

}
