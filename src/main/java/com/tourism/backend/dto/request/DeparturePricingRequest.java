package com.tourism.backend.dto.request;

import com.tourism.backend.enums.PassengerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeparturePricingRequest {
    @NotNull(message = "Loại hành khách không được để trống")
    private PassengerType passengerType;

    private String ageDescription;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", message = "Giá gốc phải >= 0")
    private BigDecimal originalPrice;

    @DecimalMin(value = "0.0", message = "Giá bán phải >= 0")
    private BigDecimal salePrice;
}
