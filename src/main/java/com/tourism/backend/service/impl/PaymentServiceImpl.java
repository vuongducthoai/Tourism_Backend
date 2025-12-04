package com.tourism.backend.service.impl;

import com.tourism.backend.config.VNPayConfig;
import com.tourism.backend.dto.request.PaymentRequestDTO;
import com.tourism.backend.dto.response.PaymentResponseDTO;
import com.tourism.backend.entity.Booking;
import com.tourism.backend.entity.Payment;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.PaymentMethod;
import com.tourism.backend.enums.PaymentStatus;
import com.tourism.backend.repository.BookingRepository;
import com.tourism.backend.repository.PaymentRepository;
import com.tourism.backend.service.PaymentService;
import com.tourism.backend.util.VNPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig vnPayConfig;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDTO createVNPayPayment(PaymentRequestDTO request, HttpServletRequest httpRequest) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!BookingStatus.PENDING_PAYMENT.equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking is not in pending payment status");
        }

        try {
            // ✅ Tạo transaction reference (phải unique)
            String vnp_TxnRef = booking.getBookingCode() + "_" + System.currentTimeMillis();

            // ✅ Amount phải nhân 100 (VNPay yêu cầu)
            long amount = request.getAmount() * 100;

            // ✅ IP Address - FIXED: Convert IPv6 to IPv4
            String vnp_IpAddr = VNPayUtil.getIpAddress(httpRequest);
            if (vnp_IpAddr.contains(":")) {
                vnp_IpAddr = "127.0.0.1"; // Convert IPv6 localhost to IPv4
            }

            // ✅ Tạo datetime
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            // ✅ Expire sau 15 phút
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());

            // ✅ Build parameters (sử dụng TreeMap để tự động sort)
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", request.getOrderInfo());
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // ✅ CRITICAL FIX: Build hash data WITHOUT encoding
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                if (fieldValue != null && fieldValue.length() > 0) {
                    // ⚠️ IMPORTANT: Hash data KHÔNG encode
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(fieldName).append('=').append(fieldValue);

                    // ✅ Query string CÓ encode
                    if (query.length() > 0) {
                        query.append('&');
                    }
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                }
            }

            String queryUrl = query.toString();

            // ✅ Tạo secure hash từ hash data (KHÔNG encode)
            String vnp_SecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            String paymentUrl = vnPayConfig.getApiUrl() + "?" + queryUrl;

            // ✅ Log để debug
            log.info("=== VNPay Payment URL ===");
            log.info("TxnRef: {}", vnp_TxnRef);
            log.info("Amount: {}", amount);
            log.info("IP Address: {}", vnp_IpAddr);
            log.info("Hash Data (NOT encoded): {}", hashData.toString());
            log.info("Secure Hash: {}", vnp_SecureHash);
            log.info("Payment URL: {}", paymentUrl);
            log.info("========================");

            // Save payment record
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setPaymentMethod(PaymentMethod.VNPAY);
            payment.setTransactionId(vnp_TxnRef);
            payment.setAmount(BigDecimal.valueOf(request.getAmount()));
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            return PaymentResponseDTO.builder()
                    .code("00")
                    .message("Success")
                    .paymentUrl(paymentUrl)
                    .transactionId(vnp_TxnRef)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error creating VNPay payment", e);
            throw new RuntimeException("Error creating VNPay payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public int handleVNPayCallback(Map<String, String> params) {
        log.info("=== Handling VNPay Callback ===");
        log.info("Params: {}", params);

        String vnp_SecureHash = params.get("vnp_SecureHash");
        Map<String, String> paramsToVerify = new HashMap<>(params);
        paramsToVerify.remove("vnp_SecureHashType");
        paramsToVerify.remove("vnp_SecureHash");

        // Verify signature
        String signValue = VNPayUtil.hashAllFields(paramsToVerify, vnPayConfig.getHashSecret());

        log.info("Expected Hash: {}", signValue);
        log.info("Received Hash: {}", vnp_SecureHash);

        if (!signValue.equals(vnp_SecureHash)) {
            log.error("❌ Invalid signature!");
            return -1;
        }

        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        String vnp_BankCode = params.get("vnp_BankCode");

        // Find payment by transaction ID
        Payment payment = paymentRepository.findByTransactionId(vnp_TxnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found for txnRef: " + vnp_TxnRef));

        Booking booking = payment.getBooking();

        if ("00".equals(vnp_ResponseCode)) {
            log.info("✅ Payment SUCCESS - Booking: {}, TxnRef: {}", booking.getBookingCode(), vnp_TxnRef);

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setBankCode(vnp_BankCode);
            payment.setBankTransactionNo(vnp_TransactionNo);
            payment.setPaymentDate(LocalDateTime.now());

            booking.setBookingStatus(BookingStatus.PAID);

            // Update available slots
            int currentSlots = booking.getTourDeparture().getAvailableSlots();
            int newSlots = currentSlots - booking.getTotalPassengers();
            booking.getTourDeparture().setAvailableSlots(newSlots);

            log.info("Updated slots: {} -> {} (reduced by {})",
                    currentSlots, newSlots, booking.getTotalPassengers());

            paymentRepository.save(payment);
            bookingRepository.save(booking);

            return 1;
        } else {
            log.warn("⚠️ Payment FAILED - Booking: {}, ResponseCode: {}",
                    booking.getBookingCode(), vnp_ResponseCode);

            payment.setStatus(PaymentStatus.FAILED);
            payment.setBankCode(vnp_BankCode);
            payment.setBankTransactionNo(vnp_TransactionNo);
            paymentRepository.save(payment);

            return 0;
        }
    }
}