package com.tourism.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Getter
@Configuration
public class VNPayConfig {

    @Value("${vnpay.api-url}")
    private String apiUrl;

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.version:2.1.0}")
    private String version;

    @Value("${vnpay.command:pay}")
    private String command;

    @Value("${vnpay.order-type:other}")
    private String orderType;

    @PostConstruct
    public void init() {
        System.out.println("=== VNPay Configuration ===");
        System.out.println("API URL: " + apiUrl);
        System.out.println("TMN Code: " + tmnCode);
        System.out.println("Hash Secret: " + (hashSecret != null && hashSecret.length() > 4
                ? "***" + hashSecret.substring(hashSecret.length() - 4)
                : "NOT SET"));
        System.out.println("Return URL: " + returnUrl);
        System.out.println("===========================");

        // Validation
        if (tmnCode == null || tmnCode.isEmpty()) {
            throw new IllegalStateException("⚠️ VNPay TMN Code is not configured!");
        }
        if (hashSecret == null || hashSecret.isEmpty()) {
            throw new IllegalStateException("⚠️ VNPay Hash Secret is not configured!");
        }
    }
}