// File: com.tourism.backend.convert.ReviewConverter.java
package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.ReviewResponseDTO;
import com.tourism.backend.entity.ImageReview;
import com.tourism.backend.entity.Review;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewConverter {

    private final ModelMapper modelMapper;

    public ReviewResponseDTO toReviewResponseDTO(Review review) {
        ReviewResponseDTO dto = modelMapper.map(review, ReviewResponseDTO.class);

        // Ánh xạ Booking Code và Tour Code
        if (review.getBooking() != null) {
            dto.setBookingCode(review.getBooking().getBookingCode());
        }
        if (review.getTour() != null) {
            dto.setTourCode(review.getTour().getTourCode());
        }

        // Ánh xạ danh sách Image URLs
        if (review.getImages() != null) {
            List<String> imageUrls = review.getImages().stream()
                    .map(ImageReview::getImage)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        } else {
            dto.setImageUrls(List.of());
        }

        return dto;
    }
}