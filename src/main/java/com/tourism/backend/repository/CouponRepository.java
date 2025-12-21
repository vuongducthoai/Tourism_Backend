package com.tourism.backend.repository;

import com.tourism.backend.entity.Coupon;
import com.tourism.backend.enums.CouponType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
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

        @Query("""
       SELECT c FROM Coupon c
       WHERE c.couponType = 'GLOBAL'
       ORDER BY
           CASE
               WHEN (
                   c.startDate <= CURRENT_TIMESTAMP
                   AND c.endDate >= CURRENT_TIMESTAMP
                   AND (c.usageLimit IS NULL OR c.usageCount < c.usageLimit)
               ) THEN 1 ELSE 0
           END DESC,
           c.discountAmount DESC,
           c.createdAt DESC
       """)
        Page<Coupon> findGlobalCoupons(Pageable pageable);



        Optional<Coupon> findByCouponCode(String couponCode);

        boolean existsByCouponCode(String couponCode);

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

        @Query("""
       SELECT c FROM Coupon c
       WHERE c.couponType = 'DEPARTURE'
       ORDER BY
           CASE 
               WHEN (
                   c.startDate <= CURRENT_TIMESTAMP 
                   AND c.endDate >= CURRENT_TIMESTAMP
                   AND (c.usageLimit IS NULL OR c.usageCount < c.usageLimit)
               ) THEN 1 ELSE 0
           END DESC,
           c.discountAmount DESC,
           c.createdAt DESC
       """)
        Page<Coupon> findDepartureCoupons(Pageable pageable);


        @Query("SELECT c FROM Coupon c WHERE " +
                "(c.startDate IS NULL OR c.startDate <= :now) AND " +
                "(c.endDate IS NULL OR c.endDate >= :now) AND " +
                "(c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
        List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);

        @Query("SELECT c FROM Coupon c WHERE " +
                "c.couponType = 'GLOBAL' AND " +
                "(c.startDate IS NULL OR c.startDate <= :now) AND " +
                "(c.endDate IS NULL OR c.endDate >= :now) AND " +
                "(c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
        List<Coupon> findActiveGlobalCoupons(@Param("now") LocalDateTime now);

        @Query("SELECT c FROM Coupon c WHERE " +
                "c.couponType = 'DEPARTURE' AND " +
                "c.tourDeparture.departureID = :departureId AND " +
                "(c.startDate IS NULL OR c.startDate <= :now) AND " +
                "(c.endDate IS NULL OR c.endDate >= :now) AND " +
                "(c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
        List<Coupon> findActiveDepartureCoupons(@Param("departureId") Integer departureId,
                                                @Param("now") LocalDateTime now);


        @Query("SELECT c FROM Coupon c WHERE " +
                "c.endDate BETWEEN :now AND :tomorrow AND " +
                "(c.usageLimit IS NULL OR c.usageCount < c.usageLimit)")
        List<Coupon> findExpiringCoupons(@Param("now") LocalDateTime now,
                                         @Param("tomorrow") LocalDateTime tomorrow);


        @Query("SELECT c FROM Coupon c WHERE " +
                "(:search IS NULL OR LOWER(c.couponCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                "(:couponType IS NULL OR c.couponType = :couponType)")
        Page<Coupon> findAllWithFilters(@Param("search") String search,
                                        @Param("couponType") CouponType couponType,
                                        Pageable pageable);

        Long countByCouponType(CouponType couponType);

        @Query("SELECT c FROM Coupon c WHERE " +
                "LOWER(c.couponCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<Coupon> searchCoupons(@Param("keyword") String keyword, Pageable pageable);

        @Query("""
       SELECT c 
       FROM Coupon c
       ORDER BY 
           CASE 
               WHEN (c.startDate <= CURRENT_TIMESTAMP 
                     AND c.endDate >= CURRENT_TIMESTAMP 
                     AND (c.usageLimit IS NULL OR c.usageCount < c.usageLimit)
                     ) 
               THEN 1 ELSE 0 
           END DESC,
           c.createdAt DESC
       """)
        Page<Coupon> findAllSorted(Pageable pageable);
}
