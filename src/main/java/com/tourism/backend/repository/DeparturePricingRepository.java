package com.tourism.backend.repository;

import com.tourism.backend.entity.Coupon;
import com.tourism.backend.entity.DeparturePricing;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.enums.PassengerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeparturePricingRepository extends JpaRepository<DeparturePricing, Integer> {
    List<DeparturePricing> findByTourDeparture(TourDeparture tourDeparture);
    Optional<DeparturePricing> findByTourDepartureAndPassengerType(
            TourDeparture tourDeparture,
            PassengerType passengerType);
    @Modifying
    @Query("DELETE FROM DeparturePricing dp WHERE dp.tourDeparture = :departure")
    void deleteByTourDeparture(@Param("departure") TourDeparture departure);

    @Query("SELECT MIN(dp.salePrice) FROM DeparturePricing dp WHERE dp.tourDeparture.departureID = :departureId")
    Optional<java.math.BigDecimal> findMinPriceByDepartureId(@Param("departureId") Integer departureId);

}
