package com.tourism.backend.repository;

import com.tourism.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    //Lay coupon hop le va duoc giam nhieu nhat
//    @Query("SELECT c FROM Coupon c WHERE " +
//        "(c.tour.tourID = :tourId OR c.tourDeparture.departureID = :departureId OR (c.tour IS NULL AND c.tourDeparture IS NULL)) "+
//        "AND c.startDate <= CURRENT_TIMESTAMP AND c.end_Date >= CURRENT_TIMESTAMP " +
//            "AND c.usageCount < c.usageLimit " +
//            "ORDER BY c.discountAmount DESC")
//
//    List<Coupon> findBestCounponsForDepature(@Param("tourId") Integer tourId,
//                                             @Param("departureId") Integer departureId);

}
