// File: com.tourism.backend.service.ReviewService.java (Interface)
package com.tourism.backend.service;

import com.tourism.backend.dto.requestDTO.ReviewRequestDTO;
import com.tourism.backend.dto.response.ReviewResponse;
import com.tourism.backend.dto.response.ReviewStatistics;
import com.tourism.backend.dto.responseDTO.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO reviewRequest) throws IOException;
    ReviewResponseDTO getReviewByBookingId(Integer bookingId);
    Page<ReviewResponse> getReviewsByTourCode(String tourCode, Pageable pageable);
    ReviewStatistics getReviewStatistics(String tourCode);
}