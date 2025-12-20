package com.tourism.backend.repository;

import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Integer>, JpaSpecificationExecutor<TourDeparture> {
    @Query("SELECT d FROM TourDeparture d " +
            "JOIN d.transports t " + // Join với bảng vận chuyển
            "WHERE d.tour.tourCode = :tourCode " +
            "AND t.type = 'OUTBOUND' " + // Chỉ lấy chiều đi
            "AND t.departTime > CURRENT_TIMESTAMP " + // Lớn hơn thời điểm hiện tại
            "ORDER BY t.departTime ASC") // Sắp xếp tăng dần
    List<TourDeparture> findFutureDepartures(@Param("tourCode") String tourCode);

    @Modifying
    @Query("UPDATE TourDeparture t SET t.availableSlots = t.availableSlots - :amount " +
            "WHERE t.departureID = :id AND t.availableSlots >= :amount")
    int decreaseAvailableSlots(@Param("id") Integer id, @Param("amount") Integer amount);
    List<TourDeparture> findByTour_TourIDAndDepartureDateAfterAndStatusTrueOrderByDepartureDateAsc(
            Integer tourId,
            LocalDateTime currentDate
    );

    List<TourDeparture> findByCoupon_CouponID(Integer couponId);
    @Modifying
    @Query("UPDATE TourDeparture t SET t.coupon = NULL WHERE t.coupon.couponID = :couponId")
    void removeCouponFromDepartures(Integer couponId);
    List<TourDeparture> findAllById(Iterable<Integer> ids);

    boolean existsByTourAndDepartureDate(Tour tour, LocalDateTime departureDate);

    List<TourDeparture> findByTour(Tour tour);

    List<TourDeparture> findByTourAndStatus(Tour tour, Boolean status);

    @Query("SELECT d FROM TourDeparture d WHERE d.departureDate BETWEEN :startDate AND :endDate")
    List<TourDeparture> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM TourDeparture d " +
            "WHERE (:tourId IS NULL OR d.tour.tourID = :tourId) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR d.departureDate >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR d.departureDate <= :endDate) " +
            "AND (:status IS NULL OR d.status = :status)")
    Page<TourDeparture> findWithFilters(
            @Param("tourId") Integer tourId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") Boolean status,
            Pageable pageable);

    @Query("SELECT d FROM TourDeparture d " +
            "WHERE d.tour.tourID = :tourId " +
            "AND d.status = true " +
            "AND d.departureDate >= :today " +
            "ORDER BY d.departureDate ASC")
    List<TourDeparture> findUpcomingDeparturesByTourId(
            @Param("tourId") Integer tourId,
            @Param("today") LocalDate today);

    @Query("SELECT COUNT(d) FROM TourDeparture d " +
            "WHERE d.tour.tourID = :tourId AND d.status = true")
    Long countActiveDeparturesByTourId(@Param("tourId") Integer tourId);
}
