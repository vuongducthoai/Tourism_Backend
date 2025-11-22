package com.tourism.backend.repository;

import com.tourism.backend.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {
    Optional<Tour> findByTourCode(String tourCode);
    boolean existsByTourCode(String tourCode);
}