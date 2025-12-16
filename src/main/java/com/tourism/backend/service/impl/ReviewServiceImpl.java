// File: com.tourism.backend.service.impl.ReviewServiceImpl.java (CẬP NHẬT)
package com.tourism.backend.service.impl;

import com.tourism.backend.convert.BookingConverter;
import com.tourism.backend.convert.ReviewConverter; // Import mới
import com.tourism.backend.dto.requestDTO.ReviewRequestDTO;
import com.tourism.backend.dto.response.ReviewResponse;
import com.tourism.backend.dto.response.ReviewStatistics;
import com.tourism.backend.dto.responseDTO.BookingResponseDTO;
import com.tourism.backend.dto.responseDTO.ReviewResponseDTO; // Import mới
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageReviewRepository imageReviewRepository;
    private final ReviewConverter reviewConverter; // Inject Converter mới
    private final BookingConverter bookingConverter;
    private final WebSocketService webSocketService;
    private static final int MIN_COMMENT_LENGTH = 10;
    private static final BigDecimal COIN_RATE = new BigDecimal("1");

    // Cập nhật Interface ReviewService.java để trả về ReviewResponseDTO
    // public interface ReviewService { ReviewResponseDTO createReview(ReviewRequestDTO reviewRequest) throws IOException; }
    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO reviewRequest) throws IOException { // Đổi kiểu trả về
        // 1. Kiểm tra Booking và Tour tồn tại
        Booking booking = bookingRepository.findById(reviewRequest.getBookingID())
                .orElseThrow(() -> new RuntimeException("Booking not found: " + reviewRequest.getBookingID()));
        Tour tour = tourRepository.findById(reviewRequest.getTourID())
                .orElseThrow(() -> new RuntimeException("Tour not found: " + reviewRequest.getTourID()));

        // Kiểm tra trạng thái Booking có hợp lệ để đánh giá
        if (booking.getBookingStatus() != BookingStatus.PAID) {
            throw new RuntimeException("Booking status must be PAID to submit a review.");
        }

        User user = booking.getUser();
        if (user == null) {
            throw new RuntimeException("User not associated with booking.");
        }

        // 2. Tính điểm thưởng Coin
        int pointsAwarded = calculateCoinPoints(reviewRequest.getComment(), reviewRequest.getImages());

        // 3. Tạo Review Entity
        Review review = new Review();
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setBooking(booking);
        review.setTour(tour);
        review.setUser(user);

        // 4. Upload ảnh và tạo ImageReview Entities
        List<ImageReview> imageReviews = new ArrayList<>();
        if (reviewRequest.getImages() != null && !reviewRequest.getImages().isEmpty()) {
            for (MultipartFile file : reviewRequest.getImages()) {
                if (file.isEmpty()) continue;

                // Sử dụng CloudinaryService để upload ảnh
                String imageUrl = cloudinaryService.uploadImage(file, "review_images");

                ImageReview imageReview = new ImageReview();
                imageReview.setImage(imageUrl);
                imageReview.setReview(review); // Liên kết ngược lại
                imageReviews.add(imageReview);
            }
        }

        review.setImages(imageReviews); // Gán danh sách ảnh vào Review

        // 5. Lưu Review (sẽ cascade lưu ImageReview)
        Review savedReview = reviewRepository.save(review);

        // 6. Cập nhật Coin cho User
        if (pointsAwarded > 0) {
            BigDecimal pointsBD = BigDecimal.valueOf(pointsAwarded).multiply(COIN_RATE);
            BigDecimal newCoinBalance = user.getCoinBalance().add(pointsBD);
            user.setCoinBalance(newCoinBalance);
            userRepository.save(user); // Lưu cập nhật User
        }

        // 7. Cập nhật trạng thái Booking
        booking.setBookingStatus(BookingStatus.REVIEWED);
        bookingRepository.save(booking);
        BookingResponseDTO responseDTO = bookingConverter.convertToBookingResponseDTO(booking);
        webSocketService.notifyAdminBookingUpdate(responseDTO);
//        if (booking.getUser() != null) {
//            webSocketService.notifyUserBookingUpdate(booking.getUser().getUserID(), responseDTO);
//        }

        // 8. Chuyển đổi sang DTO trước khi trả về
        return reviewConverter.toReviewResponseDTO(savedReview); // <-- TRẢ VỀ DTO
    }

    @Override
    @Transactional(readOnly = true) // Đảm bảo Lazy Loading được xử lý trong transaction
    public ReviewResponseDTO getReviewByBookingId(Integer bookingId) {
        Review review = reviewRepository.findByBookingIdWithImages(bookingId)
                .orElseThrow(() -> new RuntimeException("Review not found for Booking ID: " + bookingId));

        return reviewConverter.toReviewResponseDTO(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByTourCode(String tourCode, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByTourCodeAndVisible(tourCode, pageable);
        return reviews.map(this::mapToResponse);
    }


    @Override
    public ReviewStatistics getReviewStatistics(String tourCode) {
        Double avgRating = reviewRepository.getAverageRatingByTourCode(tourCode);
        Integer totalReviews = reviewRepository.countByTourCode(tourCode);

        Integer fiveStars = reviewRepository.countByTourCodeAndRating(tourCode, 5);
        Integer fourStars = reviewRepository.countByTourCodeAndRating(tourCode, 4);
        Integer threeStars = reviewRepository.countByTourCodeAndRating(tourCode, 3);
        Integer twoStars = reviewRepository.countByTourCodeAndRating(tourCode, 2);
        Integer oneStar = reviewRepository.countByTourCodeAndRating(tourCode, 1);

        double total = totalReviews > 0 ? totalReviews : 1; // Tránh chia cho 0

        return ReviewStatistics.builder()
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews)
                .fiveStars(fiveStars)
                .fourStars(fourStars)
                .threeStars(threeStars)
                .twoStars(twoStars)
                .oneStar(oneStar)
                .fiveStarsPercent(Math.round((fiveStars / total) * 1000.0) / 10.0)
                .fourStarsPercent(Math.round((fourStars / total) * 1000.0) / 10.0)
                .threeStarsPercent(Math.round((threeStars / total) * 1000.0) / 10.0)
                .twoStarsPercent(Math.round((twoStars / total) * 1000.0) / 10.0)
                .oneStarPercent(Math.round((oneStar / total) * 1000.0) / 10.0)
                .build();
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewID())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .user(ReviewResponse.UserInfo.builder()
                        .userId(review.getUser().getUserID())
                        .fullName(review.getUser().getFullName())
                        .avatar(review.getUser().getAvatar())
                        .email(review.getUser().getEmail())
                        .build())
                .images(review.getImages() != null
                        ? review.getImages().stream()
                        .map(img -> img.getImage()) // Lấy field 'image' từ ImageReview
                        .collect(Collectors.toList())
                        : null)
                .tourCode(review.getTour().getTourCode())
                .tourName(review.getTour().getTourName())
                .build();
    }

    private int calculateCoinPoints(String comment, List<MultipartFile> images) {
        if (comment == null || comment.length() < MIN_COMMENT_LENGTH) {
            return 0; // Không đủ điều kiện cơ bản
        }

        int imageCount = (images != null) ? (int) images.stream().filter(file -> !file.isEmpty()).count() : 0;

        if (imageCount == 0) {
            return 5; // > 10 ký tự, không ảnh: 5 điểm
        } else if (imageCount == 1) {
            return 7; // > 10 ký tự, 1 ảnh: 7 điểm
        } else { // imageCount > 1
            return 10; // > 10 ký tự, > 1 ảnh: 10 điểm
        }
    }
}