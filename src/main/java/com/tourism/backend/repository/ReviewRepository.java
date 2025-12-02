package com.tourism.backend.repository;

import com.tourism.backend.entity.Review;
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
}
