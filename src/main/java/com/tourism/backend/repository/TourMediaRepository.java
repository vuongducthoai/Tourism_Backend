package com.tourism.backend.repository;

import com.tourism.backend.entity.TourMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourMediaRepository extends JpaRepository<TourMedia, Integer> {
}
