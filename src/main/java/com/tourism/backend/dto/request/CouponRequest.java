package com.tourism.backend.dto.request;

import com.tourism.backend.enums.CouponType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {
    @NotBlank(message = "Coupon code is required")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Coupon code must be uppercase alphanumeric")
    private String couponCode;

    private String description;

    @NotNull(message = "Discount amount is required")
    @Min(value = 1, message = "Discount amount must be greater than 0")
    private Integer discountAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private BigDecimal minOrderValue;

    @NotNull(message = "Coupon type is required")
    private CouponType couponType;

    private List<Integer> departureIds;
    Boolean sendNotification;
}
