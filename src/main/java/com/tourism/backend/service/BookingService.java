package com.tourism.backend.service;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.requestDTO.BookingSearchRequestDTO;
import com.tourism.backend.dto.requestDTO.BookingUpdateStatusRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.dto.requestDTO.BookingCancellationRequestDTO;
import com.tourism.backend.dto.requestDTO.RefundInformationRequestDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface BookingService {
    TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId);
    BookingDetailResponseDTO createBooking(BookingRequestDTO request, String authenticatedEmail);
    BookingDetailResponseDTO getBookingDetail(String bookingCode);
    List<BookingResponseDTO> getAllBookingsByUser(Integer userID, BookingStatus bookingStatus);
    BookingResponseDTO cancelBooking(BookingCancellationRequestDTO requestDTO);
    BookingResponseDTO requestRefund(Integer bookingID, RefundInformationRequestDTO refundDTO);
    Page<BookingResponseDTO> searchBookings(BookingSearchRequestDTO searchDTO, Pageable pageable);
    BookingResponseDTO updateBookingStatus(BookingUpdateStatusRequestDTO requestDTO);

}
