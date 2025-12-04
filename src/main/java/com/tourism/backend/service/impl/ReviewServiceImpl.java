// File: com.tourism.backend.service.impl.ReviewServiceImpl.java (CẬP NHẬT)
package com.tourism.backend.service.impl;

import com.tourism.backend.convert.ReviewConverter; // Import mới
import com.tourism.backend.dto.requestDTO.ReviewRequestDTO;
import com.tourism.backend.dto.responseDTO.ReviewResponseDTO; // Import mới
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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