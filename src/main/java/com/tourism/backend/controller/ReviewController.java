// File: com.tourism.backend.controller.ReviewController.java (CẬP NHẬT)
package com.tourism.backend.controller;

import com.tourism.backend.dto.requestDTO.ReviewRequestDTO;
import com.tourism.backend.dto.response.ReviewResponse;
import com.tourism.backend.dto.response.ReviewStatistics;
import com.tourism.backend.dto.responseDTO.ReviewResponseDTO; // Import DTO phản hồi mới
import com.tourism.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ReviewResponseDTO> submitReview(@ModelAttribute ReviewRequestDTO reviewRequest) {
        try {
            // Kiểm tra các trường bắt buộc
            if (reviewRequest.getRating() == null || reviewRequest.getBookingID() == null || reviewRequest.getTourID() == null) {
                // Nếu thiếu, ném lỗi để trả về HTTP 400
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating, Booking ID, and Tour ID are required.");
            }

            // Gọi service, nhận về DTO
            ReviewResponseDTO responseDto = reviewService.createReview(reviewRequest);

            // Trả về DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (RuntimeException e) {
            // Lỗi nghiệp vụ (Booking not found, status invalid, etc.)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IOException e) {
            // Lỗi khi upload ảnh
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading images: " + e.getMessage(), e);
        }
    }
    @GetMapping("/{bookingID}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Integer bookingID) {
        try {
            ReviewResponseDTO responseDto = reviewService.getReviewByBookingId(bookingID);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/tour/{tourCode}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByTour(
            @PathVariable String tourCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getReviewsByTourCode(tourCode, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/tour/{tourCode}/statistics")
    public ResponseEntity<ReviewStatistics> getReviewStatistics(@PathVariable String tourCode) {
        ReviewStatistics stats = reviewService.getReviewStatistics(tourCode);
        return ResponseEntity.ok(stats);
    }
}