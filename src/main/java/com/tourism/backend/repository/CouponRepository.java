package com.tourism.backend.repository;

import com.tourism.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
        @Query("SELECT c FROM Coupon c WHERE " +
                "c.isDeleted = false " +
                "AND c.tourDeparture IS NULL " +
                "AND c.startDate <= :now " +
                "AND c.endDate >= :now " +
                "AND c.usageCount < c.usageLimit " +
                "ORDER BY c.discountAmount DESC")
        List<Coupon> findGlobalCoupons(@Param("now") LocalDateTime now);

        @Query("SELECT c FROM Coupon c WHERE " +
                "c.isDeleted = false " +
                "AND c.tourDeparture.departureID = :departureId " +
                "AND c.startDate <= :now " +
                "AND c.endDate >= :now " +
                "AND c.usageCount < c.usageLimit " +
                "ORDER BY c.discountAmount DESC")
        List<Coupon> findByDepartureId(@Param("departureId") Integer departureId, @Param("now") LocalDateTime now);

        @Query(value = """
        SELECT * FROM coupons c 
        WHERE c.departure_id = :departureId 
        AND (c.start_date IS NULL OR c.start_date <= :now)
        AND (c.end_date IS NULL OR c.end_date >= :now)
        AND (c.usage_limit IS NULL OR c.usage_count < c.usage_limit)
        AND (c.min_order_value IS NULL OR c.min_order_value <= :orderValue)
        ORDER BY c.discount_amount DESC
        LIMIT 1
        """, nativeQuery = true)
        Optional<Coupon> findBestDepartureCoupon(
                @Param("departureId") Integer departureId,
                @Param("orderValue") BigDecimal orderValue,
                @Param("now") LocalDateTime now
        );

        /**
         * Tìm coupon global tốt nhất (departureId = NULL)
         */
        @Query(value = """
        SELECT * FROM coupons c 
        WHERE c.departure_id IS NULL 
        AND (c.start_date IS NULL OR c.start_date <= :now)
        AND (c.end_date IS NULL OR c.end_date >= :now)
        AND (c.usage_limit IS NULL OR c.usage_count < c.usage_limit)
        AND (c.min_order_value IS NULL OR c.min_order_value <= :orderValue)
        ORDER BY c.discount_amount DESC
        LIMIT 1
        """, nativeQuery = true)
        Optional<Coupon> findBestGlobalCoupon(
                @Param("orderValue") BigDecimal orderValue,
                @Param("now") LocalDateTime now
        );

}
