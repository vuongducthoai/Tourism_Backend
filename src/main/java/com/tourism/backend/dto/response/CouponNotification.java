package com.tourism.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponNotification {
    private String type;
    private String couponCode;
    private String title;
    private String message;
    private Integer discountAmount;
    private LocalDateTime timestamp;
    private String action; // URL để redirect
}
