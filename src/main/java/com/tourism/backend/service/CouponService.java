package com.tourism.backend.service;

import com.tourism.backend.dto.request.CouponRequest;
import com.tourism.backend.dto.response.CouponResponse;
import com.tourism.backend.enums.CouponType;
import com.tourism.backend.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    CouponResponse updateCoupon(Integer couponId, CouponRequest request);
    void deleteCoupon(Integer couponId);
    CouponResponse getCouponById(Integer couponId);
    Page<CouponResponse> getAllCoupons(Pageable pageable);
    List<CouponResponse> getActiveCoupons();
    List<CouponResponse> getApplicableCouponsForBooking(Integer departureId);
    CouponResponse validateCouponForBooking(String couponCode, Integer departureId);
    void notifyNewCoupon(CouponResponse coupon, NotificationType type);
    void checkAndNotifyExpiringCoupons();
    Page<CouponResponse> getGlobalCoupons(Pageable pageable);
    Page<CouponResponse> getDepartureCoupons(Pageable pageable);
    Page<CouponResponse> searchCoupons(String keyword, Pageable pageable);
}

