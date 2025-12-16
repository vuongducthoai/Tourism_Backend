package com.tourism.backend.repository;

import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourImageRepository extends JpaRepository<TourImage, Integer> {
    void deleteByTour(Tour tour);
    List<TourImage> findByTour(Tour tour);
}
