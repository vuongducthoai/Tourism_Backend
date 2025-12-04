package com.tourism.backend.repository;

import com.tourism.backend.entity.ImageReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageReviewRepository extends JpaRepository<ImageReview, Integer> {
    // Không cần phương thức đặc biệt
}