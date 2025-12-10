package com.tourism.backend.service;

import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.RefundInformation;
import com.tourism.backend.entity.User;

import java.math.BigDecimal;

public interface MailService {
    void sendRefundRequestNotification(Booking booking, RefundInformation refundInfo, BigDecimal totalRefundAmount);
    void sendPaymentConfirmationEmail(Booking booking);
    void sendCancellationEmail(Booking booking);
    void sendCancellationWithRefundEmail(Booking booking, BigDecimal refundAmount);
    void sendAccountStatusEmail(User user, Boolean status, String reason);
}