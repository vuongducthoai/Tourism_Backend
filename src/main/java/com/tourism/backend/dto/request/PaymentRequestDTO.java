package com.tourism.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    @NotBlank(message = "Booking code is required")
    private String bookingCode;
    @Min(value = 1000, message = "Amount must be at least 1000 VND")
    private Long amount;
    private String orderInfo;
    private String locale;
}