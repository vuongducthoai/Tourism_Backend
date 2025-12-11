package com.tourism.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

import jakarta.annotation.PostConstruct;

@Getter
@Configuration
@ConfigurationProperties(prefix = "payos")
public class PayOSConfig {

    @Value("${payos.client.id}")
    private String clientId;

    @Value("${payos.api.key}")
    private String apiKey;

    @Value("${payos.checksum.key}")
    private String checksumKey;

    @Value("${payos.return.url}")
    private String returnUrl;

    @Value("${payos.cancel.url}")
    private String cancelUrl;

    @Value("${payos.webhook.url:#{null}}")
    private String webhookUrl;

    private PayOS payOS;

    @PostConstruct
    public void init() {
        this.payOS = new PayOS(clientId, apiKey, checksumKey);

        System.out.println("=== PayOS Configuration ===");
        System.out.println("Client ID: " + clientId);
        System.out.println("API Key: " + (apiKey != null && apiKey.length() > 4
                ? "***" + apiKey.substring(apiKey.length() - 4)
                : "NOT SET"));
        System.out.println("Checksum Key: " + (checksumKey != null && checksumKey.length() > 4
                ? "***" + checksumKey.substring(checksumKey.length() - 4)
                : "NOT SET"));
        System.out.println("Return URL: " + returnUrl);
        System.out.println("Cancel URL: " + cancelUrl);
        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("===========================");

        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("⚠️ PayOS Client ID is not configured!");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("⚠️ PayOS API Key is not configured!");
        }
        if (checksumKey == null || checksumKey.isEmpty()) {
            throw new IllegalStateException("⚠️ PayOS Checksum Key is not configured!");
        }
    }

    @Bean
    public PayOS getPayOS() {
        return this.payOS;
    }
}