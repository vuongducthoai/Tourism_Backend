package com.tourism.backend.service.impl;

import com.google.gson.Gson;
import com.tourism.backend.config.PayOSConfig;
import com.tourism.backend.dto.payos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOSService {

    private final PayOSConfig payOSConfig;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String PAYOS_API_URL = "https://api-merchant.payos.vn/v2/payment-requests";

    /**
     * Tạo payment link với PayOS
     */
    public CheckoutResponseData createPaymentLink(PaymentData paymentData) throws Exception {
        log.info("=== Creating PayOS Payment Link ===");

        // BƯỚC 1: Tạo signature trước
        String signature = generateSignature(paymentData);
        log.info("Signature: {}", signature);

        // BƯỚC 2: Gán signature vào object request (QUAN TRỌNG: Cần đảm bảo class PaymentData có field signature)
        paymentData.setSignature(signature);

        // BƯỚC 3: Convert sang JSON sau khi đã có signature
        String requestBody = gson.toJson(paymentData);
        log.info("Request Body: {}", requestBody);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PAYOS_API_URL))
                .header("Content-Type", "application/json")
                .header("x-client-id", payOSConfig.getClientId())
                .header("x-api-key", payOSConfig.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Response Status: {}", response.statusCode());
        log.info("Response Body: {}", response.body());

        if (response.statusCode() != 200) {
            throw new RuntimeException("PayOS API error: " + response.body());
        }

        // Parse response
        PayOSResponse<CheckoutResponseData> payosResponse = gson.fromJson(
                response.body(),
                new com.google.gson.reflect.TypeToken<PayOSResponse<CheckoutResponseData>>(){}.getType()
        );

        if (!"00".equals(payosResponse.getCode())) {
            throw new RuntimeException("PayOS error: " + payosResponse.getDesc());
        }

        return payosResponse.getData();
    }

    /**
     * Lấy thông tin payment
     */
    public PaymentStatusData getPaymentInfo(Long orderCode) throws Exception {
        log.info("=== Getting Payment Info for orderCode: {} ===", orderCode);

        String url = PAYOS_API_URL + "/" + orderCode;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-client-id", payOSConfig.getClientId())
                .header("x-api-key", payOSConfig.getApiKey())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Response Status: {}", response.statusCode());
        log.info("Response Body: {}", response.body());

        if (response.statusCode() != 200) {
            throw new RuntimeException("PayOS API error: " + response.body());
        }

        PayOSResponse<PaymentStatusData> payosResponse = gson.fromJson(
                response.body(),
                new com.google.gson.reflect.TypeToken<PayOSResponse<PaymentStatusData>>(){}.getType()
        );

        return payosResponse.getData();
    }

    /**
     * Cancel payment
     */
    public void cancelPayment(Long orderCode, String cancellationReason) throws Exception {
        log.info("=== Cancelling Payment: {} ===", orderCode);

        String url = PAYOS_API_URL + "/" + orderCode + "/cancel";

        String requestBody = gson.toJson(new CancelRequest(cancellationReason));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-client-id", payOSConfig.getClientId())
                .header("x-api-key", payOSConfig.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Cancel Response: {}", response.body());
    }

    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(String webhookData, String receivedSignature) {
        try {
            String calculatedSignature = hmacSHA256(payOSConfig.getChecksumKey(), webhookData);
            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
        }
    }

    /**
     * Generate signature for payment request
     */
    private String generateSignature(PaymentData paymentData) throws Exception {
        // Sort data theo thứ tự: amount, cancelUrl, description, orderCode, returnUrl
        String data = String.format(
                "amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s",
                paymentData.getAmount(),
                paymentData.getCancelUrl(),
                paymentData.getDescription(),
                paymentData.getOrderCode(),
                paymentData.getReturnUrl()
        );

        return hmacSHA256(payOSConfig.getChecksumKey(), data);
    }

    /**
     * HMAC SHA256
     */
    private String hmacSHA256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);

        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    // Helper class for cancel request
    private static class CancelRequest {
        private String cancellationReason;

        public CancelRequest(String cancellationReason) {
            this.cancellationReason = cancellationReason;
        }
    }
}