package com.tourism.backend.repository;

import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.Payment;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findFirstByBookingAndStatusOrderByCreatedAtDesc(
            Booking booking,
            PaymentStatus status
    );

    List<Payment> findByStatusAndBooking_BookingStatusAndTimeLimitBefore(
            PaymentStatus paymentStatus,
            BookingStatus bookingStatus,
            LocalDateTime currentTime
    );

    Optional<Payment> findByBooking(Booking booking);
}
