package com.tourism.backend.repository;

import com.tourism.backend.entity.Coupon;
import com.tourism.backend.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<PostCategory, Integer> {
}
