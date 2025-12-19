package com.tourism.backend.repository;

import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.repository.custom.TourRepositoryCustom;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    Long countByStatus(Boolean status);

    @Query("SELECT COUNT(td) FROM TourDeparture td")
    Long countAllDepartures();

    @Query("SELECT COUNT(td) FROM TourDeparture td WHERE td.departureDate > :date")
    Long countUpcomingDepartures(@Param("date") LocalDateTime date);

    @Query("SELECT new com.tourism.backend.dto.responseDTO.DashboardStatsDTO$HotTour(" +
            "t.tourID, t.tourCode, t.tourName, COUNT(b.bookingID), SUM(b.totalPrice), " +
            "COALESCE(AVG(r.rating), 0.0)) " +
            "FROM Tour t " +
            "JOIN t.departures td " +
            "JOIN Booking b ON b.tourDeparture.departureID = td.departureID " +
            "LEFT JOIN Review r ON r.tour.tourID = t.tourID " +
            "WHERE b.bookingStatus = :status " +
            "GROUP BY t.tourID, t.tourCode, t.tourName " +
            "ORDER BY COUNT(b.bookingID) DESC, SUM(b.totalPrice) DESC") // <--- Thêm điều kiện sắp xếp phụ
    List<DashboardStatsDTO.HotTour> getHotToursRaw(@Param("status") BookingStatus status, Pageable pageable);

    default List<DashboardStatsDTO.HotTour> getHotTours(BookingStatus status, int limit) {
        return getHotToursRaw(status, PageRequest.of(0, limit));
    }

    @Query("SELECT t FROM Tour t WHERE t.tourID IN (" +
            "SELECT td.tour.tourID FROM TourDeparture td " +
            "JOIN Booking b ON b.tourDeparture.departureID = td.departureID " +
            "WHERE b.bookingStatus = 'PENDING_REFUND' " +
            "GROUP BY td.tour.tourID " +
            "ORDER BY COUNT(b.bookingID) DESC)")
    List<Tour> getToursWithMostRefunds(Pageable pageable);

    default List<Tour> getToursWithMostRefunds(int limit) {
        return getToursWithMostRefunds(PageRequest.of(0, limit));
    }

    @Query("SELECT t FROM Tour t WHERE t.tourID IN (" +
            "SELECT td.tour.tourID FROM TourDeparture td " +
            "LEFT JOIN Booking b ON b.tourDeparture.departureID = td.departureID " +
            "WHERE td.departureDate > CURRENT_DATE " +
            "GROUP BY td.tour.tourID " +
            "HAVING COUNT(b.bookingID) < 3 " +
            "ORDER BY COUNT(b.bookingID))")
    List<Tour> getToursWithLowBookings(Pageable pageable);

    default List<Tour> getToursWithLowBookings(int limit) {
        return getToursWithLowBookings(PageRequest.of(0, limit));
    }

    @Query("SELECT new com.tourism.backend.dto.responseDTO.DashboardStatsDTO$TourPerformance(" +
            "t.tourName, COUNT(b.bookingID), SUM(b.totalPrice), " +
            "COALESCE(AVG(r.rating), 0.0)) " +
            "FROM Tour t " +
            "JOIN t.departures td " +
            "JOIN Booking b ON b.tourDeparture.departureID = td.departureID " +
            "LEFT JOIN Review r ON r.tour.tourID = t.tourID " +
            "WHERE b.bookingStatus = :status " +
            "GROUP BY t.tourName " +
            "ORDER BY SUM(b.totalPrice) DESC")
    List<DashboardStatsDTO.TourPerformance> getTourPerformanceRaw(
            @Param("status") BookingStatus status,
            Pageable pageable
    );

    default List<DashboardStatsDTO.TourPerformance> getTourPerformance(BookingStatus status, int limit) {
        return getTourPerformanceRaw(status, PageRequest.of(0, limit));
    }
}