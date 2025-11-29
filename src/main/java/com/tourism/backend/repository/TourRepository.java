package com.tourism.backend.repository;

import com.tourism.backend.entity.Tour;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {
    Optional<Tour> findByTourCode(String tourCode);
    boolean existsByTourCode(String tourCode);

    /**
     * Lấy tất cả Tour, JOIN FETCH Location và Main Image.
     * Cần @Transactional ở Service để truy cập Lazy Collections (departures, pricings) sau này.
     */
    @Query("""
        SELECT t 
        FROM Tour t
        LEFT JOIN FETCH t.startLocation sl
        LEFT JOIN FETCH t.images img 
        WHERE img.isMainImage = TRUE OR img IS NULL
        """)
    List<Tour> findAllToursForListDisplay();


    @Query("SELECT t FROM Tour t " +
            "WHERE t.endLocation.locationID =:locationId " +
            "AND t.tourID <> :excludeTourId"
    )
    List<Tour> findRelatedTours(@Param("locationId") Integer locationId,
                                @Param("excludeTourId") Integer excludeTourId,
                                Pageable pageable
                                );
}