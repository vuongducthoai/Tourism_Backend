package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@EqualsAndHashCode(callSuper = true)
public class Coupon extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponID;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Coupon code is required")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Coupon code must be uppercase alphanumeric without spaces")
    private String couponCode;

    @NotNull(message = "Discount amount is required")
    @Min(value = 1, message = "Discount amount must be greater than 0")
    private Integer discountAmount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "min_order_value")
    private BigDecimal minOrderValue;

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return (startDate == null || now.isAfter(startDate)) &&
                (endDate == null || now.isBefore(endDate)) &&
                (usageLimit == null || usageCount < usageLimit);
    }
}