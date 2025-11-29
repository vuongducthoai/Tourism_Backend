package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailResponseDTO {

    // --- 1. THÔNG TIN CƠ BẢN (HEADER) ---
    private Integer tourId;
    private String tourCode;
    private String tourName;
    private String duration;
    private String transportation;
    private String attractions;
    private String meals;
    private String suitableCustomer;
    private String startLocation;
    private String endLocation;
    private String airportName;
    private String tripTransportation;
    private String idealTime;

    // --- 2. THÔNG TIN GIÁ TỔNG QUAN (HEADER PRICING) ---
    // Dùng để hiển thị giá "tốt nhất" hoặc "giá từ" ở ngay đầu trang
    private BigDecimal originalPrice;       // Giá gốc (Gạch ngang)
    private BigDecimal salePrice;           // Giá bán (Màu đỏ)
    private BigDecimal couponDiscount;      // Tiền giảm từ Coupon
    private String bestCouponCode;          // Mã coupon áp dụng
    private BigDecimal finalPrice;          // Giá cuối cùng (Sau khi trừ hết)
    private Integer totalDiscountPercentage; // % Giảm tổng

    // --- 3. CÁC DANH SÁCH CHI TIẾT ---
    private List<String> images;            // Gallery ảnh
    private List<ItineraryDTO> itinerary;   // Lịch trình chi tiết
    private List<DepartureDTO> departures;  // Lịch khởi hành (Calendar)
    private PolicyDTO policy;               // Chính sách
    private BranchContactDTO branchContact;

    // ==========================================
    //      INNER CLASSES (DTO CON)
    // ==========================================

    @Data
    @Builder
    public static class ItineraryDTO {
        private Integer dayNumber;
        private String title;
        private String meals;
        private String details; // HTML content
    }

    @Data
    @Builder
    public static class DepartureDTO {
        private Integer departureId;
        private LocalDate departureDate;
        private Integer availableSlots;
        private List<TransportDTO> transports;
        private List<PricingDTO> pricings;

        // Coupon Departure-specific
        private String departureCouponCode;
        private BigDecimal departureCouponDiscount;

        // Coupon Global
        private String globalCouponCode;
        private BigDecimal globalCouponDiscount;

        // Tổng discount từ cả 2 coupon
        private BigDecimal totalDiscountAmount;
    }

    @Data
    @Builder
    public static class TransportDTO {
        private String type; // OUTBOUND / INBOUND
        private String transportCode;
        private String vehicleName;
        private String startPoint;
        private String startPointName;
        private String endPoint;
        private String endPointName;
        private LocalDateTime departTime;
        private LocalDateTime arrivalTime;
    }

    @Data
    @Builder
    public static class PricingDTO {
        private String passengerType; // ADULT, CHILD, SINGLE_SUPPLEMENT
        private String description;
        private BigDecimal originalPrice;
        private BigDecimal salePrice;
        private BigDecimal finalPrice;
    }

    @Data
    @Builder
    public static class PolicyDTO {
        private String templateName;
        private String childPricingNotes;
        private String registrationConditions;
        private String regularDayCancellationRules;
        private String holidayCancellationRules;
        private String forceMajeureRules;
        private String packingList;
        private String paymentConditions;
        private String cancellationRules;
        private String tourPriceIncludes;
        private String tourPriceExcludes;
    }

    @Data
    @Builder
    public static class BranchContactDTO {
        private String branchName;
        private String phone;
        private String email;
        private String address;
        private Boolean isHeadOffice = false;
    }
}