// src/main/java/com/tourism/backend/dto/responseDTO/DashboardStatsDTO.java
package com.tourism.backend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // Main fields
    private UserStats userStats;
    private RevenueStats revenueStats;
    private BookingStats bookingStats;
    private TourStats tourStats;
    private List<RecentActivity> recentActivities;
    private AIAnalysis aiAnalysis;
    private ChartsData chartsData;

    // ============================================
    // NESTED STATIC CLASSES (Inner Classes)
    // ============================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long lockedUsers;
        private Long newUsersToday;
        private Long newUsersThisWeek;
        private Long newUsersThisMonth;
        private Double userGrowthRate;
        private List<DailyUserGrowth> dailyGrowth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueStats {
        private BigDecimal totalRevenue; // PAID
        private BigDecimal pendingConfirmation;
        private BigDecimal pendingPayment;
        private BigDecimal cancelledRevenue;
        private BigDecimal todayRevenue;
        private BigDecimal thisWeekRevenue;
        private BigDecimal thisMonthRevenue;
        private BigDecimal lastMonthRevenue;
        private Double revenueGrowthRate;
        private List<DailyRevenue> dailyRevenue;
        private BigDecimal pendingRefund;
        private Map<String, BigDecimal> revenueByTour;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingStats {
        private Long totalBookings;
        private Long paidBookings;
        private Long pendingConfirmation;
        private Long pendingPayment;
        private Long pendingRefund;
        private Long cancelledBookings;
        private Long todayBookings;
        private Long thisWeekBookings;
        private Double conversionRate;
        private List<BookingStatusCount> statusDistribution;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourStats {
        private Long totalTours;
        private Long activeTours;
        private Long totalDepartures;
        private Long upcomingDepartures;
        private List<HotTour> hotTours;
        private List<TourNeedingAttention> toursNeedingAttention;
        private Double averageRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String type;
        private String description;
        private LocalDate timestamp;
        private String severity;
        private String relatedCode;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AIAnalysis {
        private String summary;
        private List<Insight> insights;
        private List<Prediction> predictions;
        private List<Recommendation> recommendations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Insight {
        private String title;
        private String description;
        private String type;
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prediction {
        private String metric;
        private String prediction;
        private Double confidence;
        private String timeframe;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String title;
        private String description;
        private String action;
        private Integer impact;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartsData {
        private List<DailyRevenue> revenueChart;
        private List<DailyUserGrowth> userGrowthChart;
        private List<BookingStatusCount> bookingStatusChart;
        private List<TourPerformance> tourPerformanceChart;
    }

    // ============================================
    // DATA CLASSES (Dùng cho Charts & Statistics)
    // ============================================

    /**
     * ✅ Class này dùng cho User Growth Chart
     * Được gọi trong UserRepository
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyUserGrowth {
        private LocalDate date;
        private Long newUsers;
        private Long totalUsers;
    }

    /**
     * ✅ Class này dùng cho Revenue Chart
     * Được gọi trong BookingRepository
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal revenue;
        private Long bookingCount;
    }

    /**
     * ✅ Class này dùng cho Booking Status Distribution
     * Được gọi trong BookingRepository
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingStatusCount {
        private String status;
        private Long count;
        private BigDecimal revenue;
    }

    /**
     * ✅ Class này dùng cho Hot Tours Section
     * Được gọi trong TourRepository
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotTour {
        private Integer tourId;
        private String tourCode;
        private String tourName;
        private Long bookingCount;
        private BigDecimal revenue;
        private Double averageRating;
    }

    /**
     * ✅ Class này dùng cho Tours Needing Attention
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourNeedingAttention {
        private Integer tourId;
        private String tourCode;
        private String tourName;
        private String reason;
        private String urgency;
    }

    /**
     * ✅ Class này dùng cho Tour Performance Chart
     * Được gọi trong TourRepository
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TourPerformance {
        private String tourName;
        private Long bookings;
        private BigDecimal revenue;
        private Double rating;
    }
}