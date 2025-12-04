package com.tourism.backend.controller;

import com.tourism.backend.dto.request.PaymentRequestDTO;
import com.tourism.backend.dto.response.PaymentResponseDTO;
import com.tourism.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @PostMapping("/vnpay/create")
    public ResponseEntity<?> createVNPayPayment(
            @RequestBody PaymentRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            log.info("=== Creating VNPay payment ===");
            log.info("Booking Code: {}", request.getBookingCode());
            log.info("Amount: {}", request.getAmount());

            // Validate request
            if (request.getBookingCode() == null || request.getBookingCode().isEmpty()) {
                throw new IllegalArgumentException("Booking code is required");
            }
            if (request.getAmount() == null || request.getAmount() <= 0) {
                throw new IllegalArgumentException("Amount must be greater than 0");
            }

            PaymentResponseDTO response = paymentService.createVNPayPayment(request, httpRequest);

            log.info("✅ Payment created successfully");
            log.info("Payment URL: {}", response.getPaymentUrl());
            log.info("Transaction ID: {}", response.getTransactionId());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("❌ Validation error: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "code", "VALIDATION_ERROR",
                            "message", e.getMessage()
                    ));
        } catch (RuntimeException e) {
            log.error("❌ Business logic error: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "code", "PAYMENT_ERROR",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("❌ Unexpected error creating payment", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "code", "INTERNAL_ERROR",
                            "message", "Đã xảy ra lỗi khi tạo thanh toán. Vui lòng thử lại."
                    ));
        }
    }

    @GetMapping("/vnpay-callback")
    public void vnpayCallback(
            @RequestParam Map<String, String> params,
            HttpServletResponse response) throws IOException {
        log.info("=== VNPay Callback Received ===");
        log.info("Response Code: {}", params.get("vnp_ResponseCode"));
        log.info("Transaction Ref: {}", params.get("vnp_TxnRef"));
        log.info("Order Info: {}", params.get("vnp_OrderInfo"));

        String bookingCode = null;

        try {
            // Extract booking code from order info
            bookingCode = extractBookingCodeFromCallback(params);
            log.info("Extracted Booking Code: {}", bookingCode);

            int result = paymentService.handleVNPayCallback(params);

            // Redirect based on result
            if (result == 1) {
                log.info("✅ Payment SUCCESS for booking: {}", bookingCode);
                response.sendRedirect(String.format("%s/payment-success?bookingCode=%s",
                        frontendUrl, bookingCode));
            } else if (result == 0) {
                log.warn("⚠️ Payment FAILED for booking: {}", bookingCode);
                response.sendRedirect(String.format("%s/payment-failed?bookingCode=%s",
                        frontendUrl, bookingCode));
            } else {
                log.error("❌ Invalid signature for booking: {}", bookingCode);
                response.sendRedirect(String.format("%s/payment-error?reason=invalid_signature",
                        frontendUrl));
            }
        } catch (Exception e) {
            log.error("❌ Callback processing error", e);
            String errorParam = bookingCode != null ?
                    String.format("?bookingCode=%s&error=%s", bookingCode, e.getMessage()) :
                    "?error=" + e.getMessage();
            response.sendRedirect(frontendUrl + "/payment-error" + errorParam);
        }
    }

    /**
     * Extracts booking code from VNPay callback parameters.
     * Expects order info format: "Booking {BOOKING_CODE}"
     */
    private String extractBookingCodeFromCallback(Map<String, String> params) {
        String orderInfo = params.get("vnp_OrderInfo");

        if (orderInfo != null && orderInfo.startsWith("Booking ")) {
            String code = orderInfo.substring(8).trim(); // Remove "Booking " prefix
            if (!code.isEmpty()) {
                return code;
            }
        }

        // Fallback to transaction reference if extraction fails
        log.warn("Could not extract booking code from order info: {}. Using vnp_TxnRef", orderInfo);
        return params.get("vnp_TxnRef");
    }
}