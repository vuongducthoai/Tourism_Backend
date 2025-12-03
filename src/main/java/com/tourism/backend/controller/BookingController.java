package com.tourism.backend.controller;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public ResponseEntity<BookingDetailResponseDTO> createBooking(@RequestBody BookingRequestDTO request) {
        BookingDetailResponseDTO response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment/{bookingCode}")
    public ResponseEntity<BookingDetailResponseDTO> getBookingDetail(
            @PathVariable String bookingCode
    ) {
        BookingDetailResponseDTO response = bookingService.getBookingDetail(bookingCode);
        return ResponseEntity.ok(response);
    }
}
