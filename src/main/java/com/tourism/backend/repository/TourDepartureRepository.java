package com.tourism.backend.repository;

import com.tourism.backend.entity.TourDeparture;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourDepartureRepository extends JpaRepository<TourDeparture, Integer> {
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
            LocalDate currentDate
    );

    List<TourDeparture> findByCoupon_CouponID(Integer couponId);
    @Modifying
    @Query("UPDATE TourDeparture t SET t.coupon = NULL WHERE t.coupon.couponID = :couponId")
    void removeCouponFromDepartures(Integer couponId);
}
