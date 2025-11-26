package com.tourism.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DeparturePricingDTO {
    private String passengerType;
    private String ageDescription;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private BigDecimal couponDiscount;
    private String couponCode;
    private BigDecimal finalPrice;
    private Integer totalDiscountPercentage;
}
