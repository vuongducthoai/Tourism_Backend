package com.tourism.backend.repository;

import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.repository.custom.TourRepositoryCustom;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer>, TourRepositoryCustom {
    Optional<Tour> findByTourCode(String tourCode);
    boolean existsByTourCode(String tourCode);

 
    @Query("""
        SELECT t 
        FROM Tour t
        LEFT JOIN FETCH t.startLocation sl
        LEFT JOIN FETCH t.images img 
        WHERE (img.isMainImage = TRUE OR img IS NULL)
        AND t.status = TRUE      
        """)
    List<Tour> findAllToursForListDisplay();

    @Query("""
        SELECT t 
        FROM Tour t
        LEFT JOIN FETCH t.startLocation sl
        LEFT JOIN FETCH t.departures td 
        WHERE t.status = TRUE
        """)
    List<Tour> findAllToursWithPricingAndTransport();

    @Query("SELECT t FROM Tour t " +
            "WHERE t.endLocation.locationID =:locationId " +
            "AND t.tourID <> :excludeTourId"
    )
    List<Tour> findRelatedTours(@Param("locationId") Integer locationId,
                                @Param("excludeTourId") Integer excludeTourId,
                                Pageable pageable
                                );

}