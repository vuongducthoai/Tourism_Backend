package com.tourism.backend.repository;
import com.tourism.backend.entity.Booking;
import java.util.Optional;
import com.tourism.backend.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
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
}

