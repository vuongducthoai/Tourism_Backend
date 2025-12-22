package com.tourism.backend.service.impl;

import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.Payment;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.PaymentStatus;
import com.tourism.backend.repository.BookingRepository;
import com.tourism.backend.repository.PaymentRepository;
import com.tourism.backend.service.BookingCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCleanupServiceImpl implements BookingCleanupService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scanOverduePayments() {
        LocalDateTime now = LocalDateTime.now();

        List<Payment> overduePayments = paymentRepository.findByStatusAndBooking_BookingStatusAndTimeLimitBefore(
                PaymentStatus.PENDING,
                BookingStatus.PENDING_PAYMENT,
                now
        );

        if (overduePayments.isEmpty()) {
            System.out.println("12333");
            return;
        }

        log.info("Tìm thấy {} giao dịch quá hạn thanh toán.", overduePayments.size());

        for (Payment payment : overduePayments) {
            try {
                processOverduePayment(payment);
            } catch (Exception e) {
                log.error("Lỗi khi xử lý quá hạn cho payment ID: {}", payment.getPaymentID(), e);
            }
        }
    }

    @Override
    public void processOverduePayment(Payment payment) {
        Booking booking = payment.getBooking();

        log.info("Cập nhật quá hạn cho Booking Code: {}", booking.getBookingCode());

        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentDescription("Hết thời gian thanh toán (Auto expired)");

        booking.setBookingStatus(BookingStatus.OVERDUE_PAYMENT);

        paymentRepository.save(payment);
        bookingRepository.save(booking);
    }
}
