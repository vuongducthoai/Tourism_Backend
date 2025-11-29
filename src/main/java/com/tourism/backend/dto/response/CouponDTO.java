package com.tourism.backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
    private Integer couponId;
    private String code;
    private String description;
    private Integer discountAmount;
    private BigDecimal minOrderValue;
}