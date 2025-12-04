// File: com.tourism.backend.service.ReviewService.java (Interface)
package com.tourism.backend.service;

import com.tourism.backend.dto.requestDTO.ReviewRequestDTO;
import com.tourism.backend.dto.responseDTO.ReviewResponseDTO;
import com.tourism.backend.entity.Review;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO reviewRequest) throws IOException;
    ReviewResponseDTO getReviewByBookingId(Integer bookingId);
}