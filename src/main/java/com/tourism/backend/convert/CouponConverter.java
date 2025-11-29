package com.tourism.backend.convert;

import com.tourism.backend.dto.response.CouponDTO;
import com.tourism.backend.entity.Coupon;

public class CouponConverter {
    private CouponDTO mapToCouponDTO(Coupon coupon) {
        return new CouponDTO(
                coupon.getCouponID(),
                coupon.getCouponCode(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getMinOrderValue()   // Điều kiện đơn tối thiểu
        );
    }
}
