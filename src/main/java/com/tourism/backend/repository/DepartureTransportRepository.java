package com.tourism.backend.repository;

import com.tourism.backend.entity.DeparturePricing;
import com.tourism.backend.entity.DepartureTransport;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.enums.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartureTransportRepository extends JpaRepository<DepartureTransport, Integer> {
    List<DepartureTransport> findByTourDeparture(TourDeparture tourDeparture);
    List<DepartureTransport> findByTourDepartureAndType(
            TourDeparture tourDeparture,
            TransportType type);
    @Modifying
    @Query("DELETE FROM DepartureTransport dt WHERE dt.tourDeparture = :departure")
    void deleteByTourDeparture(@Param("departure") TourDeparture departure);

    long countByTourDeparture(TourDeparture tourDeparture);

}
