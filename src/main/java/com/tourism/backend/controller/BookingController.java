package com.tourism.backend.controller;

import com.tourism.backend.dto.requestDTO.BookingCancellationRequestDTO;
import com.tourism.backend.dto.requestDTO.RefundInformationRequestDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/order")
    public ResponseEntity<TourBookingInfoDTO> getBookingInitInfo(
            @RequestParam String tourCode,
            @RequestParam(required = false) Integer departureId
    ) {
        TourBookingInfoDTO response = bookingService.getTourBookingInfo(tourCode, departureId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<?> getAllBookingsByUser(
            @PathVariable Integer userID,
            @RequestParam(required = false) String bookingStatus
    ) {
        BookingStatus status = null;
        if (bookingStatus != null && !bookingStatus.trim().isEmpty()) {
            try {
                status = BookingStatus.valueOf(bookingStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                ErrorResponseDTO error = new ErrorResponseDTO(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        "Invalid booking status: " + bookingStatus
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }

        // --- BỎ KHỐI TRY-CATCH LỚN ĐỂ LỖI GỐC ĐƯỢC IN RA ---
        List<BookingResponseDTO> bookings = bookingService.getAllBookingsByUser(userID, status);
        return ResponseEntity.ok(bookings);
        // --------------------------------------------------------
    }
    // API MỚI: Hủy Booking và Hoàn tiền
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelBooking(
            @Valid @RequestBody BookingCancellationRequestDTO requestDTO
    ) {
        try {
            BookingResponseDTO cancelledBooking = bookingService.cancelBooking(requestDTO);
            return ResponseEntity.ok(cancelledBooking);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Cancellation Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi hủy booking: " + e.getMessage()
            );
            // In stack trace ra console để debug
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @PostMapping("/refund-request/{bookingID}")
    public ResponseEntity<?> requestRefund(
            @PathVariable Integer bookingID,
            @Valid @RequestBody RefundInformationRequestDTO refundDTO
    ) {
        try {
            // Sử dụng @Valid để kích hoạt validation trong DTO
            BookingResponseDTO updatedBooking = bookingService.requestRefund(bookingID, refundDTO);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Refund Request Error",
                    e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi yêu cầu hoàn tiền: " + e.getMessage()
            );
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
