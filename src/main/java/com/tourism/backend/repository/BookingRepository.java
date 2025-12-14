package com.tourism.backend.repository;
import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;
import com.tourism.backend.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.repository.custom.BookingRepositoryCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>, BookingRepositoryCustom {
    @Query("""
    SELECT DISTINCT b FROM Booking b
    LEFT JOIN FETCH b.tourDeparture td
    LEFT JOIN FETCH td.tour t
    LEFT JOIN FETCH b.payment p
    WHERE b.user.userID = :userID
    AND (:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus)
    ORDER BY td.departureDate DESC, b.bookingDate DESC
    """)
    List<Booking> findByUserIDWithDetails(
            @Param("userID") Integer userID,
            @Param("bookingStatus") BookingStatus bookingStatus
    );
           
    Optional<Booking> findByBookingCode(String bookingCode);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.bookingStatus = :status")
    BigDecimal sumTotalPriceByStatus(@Param("status") BookingStatus status);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b " +
            "WHERE b.bookingDate BETWEEN :start AND :end AND b.bookingStatus = :status")
    BigDecimal sumRevenueByDateAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );

    @Query("SELECT new com.tourism.backend.dto.responseDTO.DashboardStatsDTO$DailyRevenue(" +
            "CAST(b.bookingDate AS LocalDate), SUM(b.totalPrice), COUNT(b)) " +
            "FROM Booking b WHERE b.bookingDate BETWEEN :start AND :end AND b.bookingStatus = :status " +
            "GROUP BY CAST(b.bookingDate AS LocalDate) ORDER BY CAST(b.bookingDate AS LocalDate)")
    List<DashboardStatsDTO.DailyRevenue> getDailyRevenue(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );

    @Query("SELECT new com.tourism.backend.dto.responseDTO.DashboardStatsDTO$BookingStatusCount(" +
            "CAST(b.bookingStatus AS string), COUNT(b), SUM(b.totalPrice)) " +
            "FROM Booking b GROUP BY b.bookingStatus")
    List<DashboardStatsDTO.BookingStatusCount> getBookingStatusDistribution();

    @Query("SELECT new map(t.tourCode as tourCode, SUM(b.totalPrice) as revenue) " +
            "FROM Booking b JOIN b.tourDeparture td JOIN td.tour t " +
            "WHERE b.bookingStatus = :status " +
            "GROUP BY t.tourCode ORDER BY SUM(b.totalPrice) DESC")
    List<Map<String, Object>> getRevenueByTourRaw(@Param("status") BookingStatus status, Pageable pageable);

    default Map<String, BigDecimal> getRevenueByTour(BookingStatus status, int limit) {
        List<Map<String, Object>> results = getRevenueByTourRaw(status, PageRequest.of(0, limit));
        return results.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("tourCode"),
                        m -> (BigDecimal) m.get("revenue")
                ));
    }

    Long countByBookingStatus(BookingStatus status);

    Long countByBookingDateBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findTop5ByBookingStatusOrderByCreatedAtDesc(BookingStatus status);
}

