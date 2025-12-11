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

    // --- (HEADER) ---
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

    // --- 2. HEADER PRICING ---
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private BigDecimal couponDiscount;
    private String bestCouponCode;
    private BigDecimal finalPrice;
    private Integer totalDiscountPercentage;

    private List<String> images;
    private String videoUrl;
    private List<ItineraryDTO> itinerary;
    private List<DepartureDTO> departures;
    private PolicyDTO policy;
    private BranchContactDTO branchContact;

    @Data
    @Builder
    public static class ItineraryDTO {
        private Integer dayNumber;
        private String title;
        private String meals;
        private String details;
    }

    @Data
    @Builder
    public static class DepartureDTO {
        private Integer departureId;
        private LocalDate departureDate;
        private Integer availableSlots;
        private List<TransportDTO> transports;
        private List<PricingDTO> pricings;

        private String departureCouponCode;
        private BigDecimal departureCouponDiscount;

        private String globalCouponCode;
        private BigDecimal globalCouponDiscount;

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