package com.tourism.backend.controller;

import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
