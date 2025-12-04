package com.tourism.backend.service;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.dto.requestDTO.BookingCancellationRequestDTO;
import com.tourism.backend.dto.requestDTO.RefundInformationRequestDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId);
    BookingDetailResponseDTO createBooking(BookingRequestDTO request);
    BookingDetailResponseDTO getBookingDetail(String bookingCode);
    List<BookingResponseDTO> getAllBookingsByUser(Integer userID, BookingStatus bookingStatus);
    BookingResponseDTO cancelBooking(BookingCancellationRequestDTO requestDTO);
    BookingResponseDTO requestRefund(Integer bookingID, RefundInformationRequestDTO refundDTO);
}
