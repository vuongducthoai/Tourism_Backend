package com.tourism.backend.repository;

import com.tourism.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByBookingCode(String bookingCode);
}
