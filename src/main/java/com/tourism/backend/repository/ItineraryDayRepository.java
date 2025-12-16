package com.tourism.backend.repository;

import com.tourism.backend.entity.ItineraryDay;
import com.tourism.backend.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryDayRepository extends JpaRepository<ItineraryDay, Integer> {
    void deleteByTour(Tour tour);
}
