package com.tourism.backend.service;

import com.tourism.backend.entity.Payment;

public interface BookingCleanupService {
    void scanOverduePayments();
    void processOverduePayment(Payment payment);
}
