package com.tourism.backend.repository;

import com.tourism.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // ✨ TRUY VẤN MỚI
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.images WHERE r.booking.bookingID = :bookingId")
    Optional<Review> findByBookingIdWithImages(@Param("bookingId") Integer bookingId);

    @Query("SELECT DISTINCT r FROM Review r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.tour " +
            "LEFT JOIN FETCH r.images " +
            "WHERE r.tour.tourCode = :tourCode AND r.isVisible = true " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findByTourCodeAndVisible(@Param("tourCode") String tourCode, Pageable pageable);

    // Tính average rating theo tourCode
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tour.tourCode = :tourCode AND r.isVisible = true")
    Double getAverageRatingByTourCode(@Param("tourCode") String tourCode);

    // Đếm số lượng review theo rating và tourCode
    @Query("SELECT COUNT(r) FROM Review r WHERE r.tour.tourCode = :tourCode AND r.rating = :rating AND r.isVisible = true")
    Integer countByTourCodeAndRating(@Param("tourCode") String tourCode, @Param("rating") Integer rating);

    // Tổng số reviews theo tourCode
    @Query("SELECT COUNT(r) FROM Review r WHERE r.tour.tourCode = :tourCode AND r.isVisible = true")
    Integer countByTourCode(@Param("tourCode") String tourCode);
}
