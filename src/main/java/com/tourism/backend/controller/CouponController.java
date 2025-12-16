package com.tourism.backend.controller;

import com.tourism.backend.dto.request.CouponRequest;
import com.tourism.backend.dto.response.CouponResponse;
import com.tourism.backend.service.impl.CouponServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponServiceImpl couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Cập nhật coupon
    @PutMapping("/{couponId}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Integer couponId,
            @Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.updateCoupon(couponId, request);
        return ResponseEntity.ok(response);
    }

    // Xóa coupon
    @DeleteMapping("/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Integer couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CouponResponse>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CouponResponse> coupons = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(coupons);
    }

    // Lấy global coupons
    @GetMapping("/global")
    public ResponseEntity<Page<CouponResponse>> getGlobalCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(couponService.getGlobalCoupons(pageable));
    }

    // Lấy departure coupons
    @GetMapping("/departure")
    public ResponseEntity<Page<CouponResponse>> getDepartureCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(couponService.getDepartureCoupons(pageable));
    }

    // Search coupons
    @GetMapping("/search")
    public ResponseEntity<Page<CouponResponse>> searchCoupons(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(couponService.searchCoupons(keyword, pageable));
    }

    // Lấy coupon theo ID
    @GetMapping("/{couponId}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Integer couponId) {
        return ResponseEntity.ok(couponService.getCouponById(couponId));
    }

}
