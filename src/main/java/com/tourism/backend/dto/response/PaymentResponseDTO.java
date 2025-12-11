package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String code;
    private String message;
    private String paymentUrl;
    private String transactionId;
    private String qrCode;
    private String status;
    private BigDecimal amount;
}