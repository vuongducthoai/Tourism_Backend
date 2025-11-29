package com.tourism.backend.controller;

import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
