package com.tourism.backend.service.impl;

import com.tourism.backend.dto.responseDTO.DashboardStatsDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.Role; // [QUAN TRỌNG]
import com.tourism.backend.repository.*;
import com.tourism.backend.service.DashboardService;
import com.tourism.backend.service.GeminiAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TourRepository tourRepository;
    private final ReviewRepository reviewRepository;
    private final GeminiAIService geminiAIService;

    // ========================================================================
    // API 1: LẤY SỐ LIỆU THỐNG KÊ
    // ========================================================================
    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStatistics() {
        log.info("🎯 Fetching dashboard statistics (Data Only)...");

        DashboardStatsDTO.UserStats userStats = getUserStatistics();
        DashboardStatsDTO.RevenueStats revenueStats = getRevenueStatistics();
        DashboardStatsDTO.BookingStats bookingStats = getBookingStatistics();
        DashboardStatsDTO.TourStats tourStats = getTourStatistics();
        List<DashboardStatsDTO.RecentActivity> recentActivities = getRecentActivities();
        DashboardStatsDTO.ChartsData chartsData = getChartsData();

        return DashboardStatsDTO.builder()
                .userStats(userStats)
                .revenueStats(revenueStats)
                .bookingStats(bookingStats)
                .tourStats(tourStats)
                .recentActivities(recentActivities)
                .aiAnalysis(null)
                .chartsData(chartsData)
                .build();
    }

    // ========================================================================
    // API 2: PHÂN TÍCH AI
    // ========================================================================
    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO.AIAnalysis getDashboardAIAnalysis() {
        log.info("🤖 Generating AI Analysis via Gemini...");

        DashboardStatsDTO.UserStats userStats = getUserStatistics();
        DashboardStatsDTO.RevenueStats revenueStats = getRevenueStatistics();
        DashboardStatsDTO.BookingStats bookingStats = getBookingStatistics();
        DashboardStatsDTO.TourStats tourStats = getTourStatistics();

        String context = String.format(
                "User Statistics (Customers Only): Total=%d, Active=%d, Growth=%.2f%%. " +
                        "Revenue: Total=%.0f, This Month=%.0f, Growth=%.2f%%. " +
                        "Bookings: Total=%d, Paid=%d, Conversion=%.2f%%. " +
                        "Tours: Total=%d, Active=%d, Avg Rating=%.2f",
                userStats.getTotalUsers(), userStats.getActiveUsers(), userStats.getUserGrowthRate(),
                revenueStats.getTotalRevenue(), revenueStats.getThisMonthRevenue(), revenueStats.getRevenueGrowthRate(),
                bookingStats.getTotalBookings(), bookingStats.getPaidBookings(), bookingStats.getConversionRate(),
                tourStats.getTotalTours(), tourStats.getActiveTours(), tourStats.getAverageRating()
        );

        String aiSummary = geminiAIService.generateDashboardSummary(context);
        List<DashboardStatsDTO.Insight> insights = geminiAIService.generateInsights(context);
        List<DashboardStatsDTO.Prediction> predictions = geminiAIService.generatePredictions(context);
        List<DashboardStatsDTO.Recommendation> recommendations = geminiAIService.generateRecommendations(context);

        return DashboardStatsDTO.AIAnalysis.builder()
                .summary(aiSummary)
                .insights(insights)
                .predictions(predictions)
                .recommendations(recommendations)
                .build();
    }

    // ========================================================================
    // [UPDATED] HELPER METHODS: USER STATISTICS (CHỈ LẤY CUSTOMER)
    // ========================================================================
    private DashboardStatsDTO.UserStats getUserStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusMonths(1);

        // 1. Tổng quan User (Chỉ Customer)
        Long totalUsers = userRepository.countByRole(Role.CUSTOMER);
        Long activeUsers = userRepository.countByStatusAndRole(true, Role.CUSTOMER);
        Long lockedUsers = userRepository.countByStatusAndRole(false, Role.CUSTOMER);

        // 2. User mới theo thời gian (Chỉ Customer)
        Long newUsersToday = userRepository.countByRoleAndCreatedAtBetween(
                Role.CUSTOMER, today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );

        Long newUsersThisWeek = userRepository.countByRoleAndCreatedAtBetween(
                Role.CUSTOMER, weekAgo.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );

        Long newUsersThisMonth = userRepository.countByRoleAndCreatedAtBetween(
                Role.CUSTOMER, monthAgo.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );

        Long newUsersLastMonth = userRepository.countByRoleAndCreatedAtBetween(
                Role.CUSTOMER, monthAgo.minusMonths(1).atStartOfDay(), monthAgo.atStartOfDay()
        );

        Double userGrowthRate = calculateGrowthRate(newUsersThisMonth, newUsersLastMonth);
        List<DashboardStatsDTO.DailyUserGrowth> dailyGrowth = getDailyUserGrowth(30);

        return DashboardStatsDTO.UserStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .lockedUsers(lockedUsers)
                .newUsersToday(newUsersToday)
                .newUsersThisWeek(newUsersThisWeek)
                .newUsersThisMonth(newUsersThisMonth)
                .userGrowthRate(userGrowthRate)
                .dailyGrowth(dailyGrowth)
                .build();
    }

    // ========================================================================
    // HELPER METHODS: REVENUE STATISTICS
    // ========================================================================
    private DashboardStatsDTO.RevenueStats getRevenueStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusMonths(1);

        BigDecimal totalRevenue = bookingRepository.sumTotalPriceByStatus(BookingStatus.PAID);
        BigDecimal pendingConfirmation = bookingRepository.sumTotalPriceByStatus(BookingStatus.PENDING_CONFIRMATION);
        BigDecimal pendingPayment = bookingRepository.sumTotalPriceByStatus(BookingStatus.PENDING_PAYMENT);
        BigDecimal pendingRefund = bookingRepository.sumTotalPriceByStatus(BookingStatus.PENDING_REFUND);
        BigDecimal cancelledRevenue = bookingRepository.sumTotalPriceByStatus(BookingStatus.CANCELLED);

        BigDecimal todayRevenue = bookingRepository.sumRevenueByDateAndStatus(
                today.atStartOfDay(), today.plusDays(1).atStartOfDay(), BookingStatus.PAID
        );

        BigDecimal thisWeekRevenue = bookingRepository.sumRevenueByDateAndStatus(
                weekAgo.atStartOfDay(), today.plusDays(1).atStartOfDay(), BookingStatus.PAID
        );

        BigDecimal thisMonthRevenue = bookingRepository.sumRevenueByDateAndStatus(
                monthAgo.atStartOfDay(), today.plusDays(1).atStartOfDay(), BookingStatus.PAID
        );

        BigDecimal lastMonthRevenue = bookingRepository.sumRevenueByDateAndStatus(
                monthAgo.minusMonths(1).atStartOfDay(), monthAgo.atStartOfDay(), BookingStatus.PAID
        );

        Double revenueGrowthRate = calculateGrowthRate(thisMonthRevenue, lastMonthRevenue);
        List<DashboardStatsDTO.DailyRevenue> dailyRevenue = getDailyRevenue(30);
        Map<String, BigDecimal> revenueByTour = getRevenueByTour(10);

        return DashboardStatsDTO.RevenueStats.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .pendingConfirmation(pendingConfirmation != null ? pendingConfirmation : BigDecimal.ZERO)
                .pendingPayment(pendingPayment != null ? pendingPayment : BigDecimal.ZERO)
                .pendingRefund(pendingRefund != null ? pendingRefund : BigDecimal.ZERO)
                .cancelledRevenue(cancelledRevenue != null ? cancelledRevenue : BigDecimal.ZERO)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .thisWeekRevenue(thisWeekRevenue != null ? thisWeekRevenue : BigDecimal.ZERO)
                .thisMonthRevenue(thisMonthRevenue != null ? thisMonthRevenue : BigDecimal.ZERO)
                .lastMonthRevenue(lastMonthRevenue != null ? lastMonthRevenue : BigDecimal.ZERO)
                .revenueGrowthRate(revenueGrowthRate)
                .dailyRevenue(dailyRevenue)
                .revenueByTour(revenueByTour)
                .build();
    }

    // ========================================================================
    // HELPER METHODS: BOOKING STATISTICS
    // ========================================================================
    private DashboardStatsDTO.BookingStats getBookingStatistics() {
        Long totalBookings = bookingRepository.count();
        Long paidBookings = bookingRepository.countByBookingStatus(BookingStatus.PAID);
        Long pendingConfirmation = bookingRepository.countByBookingStatus(BookingStatus.PENDING_CONFIRMATION);
        Long pendingPayment = bookingRepository.countByBookingStatus(BookingStatus.PENDING_PAYMENT);
        Long pendingRefund = bookingRepository.countByBookingStatus(BookingStatus.PENDING_REFUND);
        Long cancelledBookings = bookingRepository.countByBookingStatus(BookingStatus.CANCELLED);

        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        Long todayBookings = bookingRepository.countByBookingDateBetween(
                today.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );

        Long thisWeekBookings = bookingRepository.countByBookingDateBetween(
                weekAgo.atStartOfDay(), today.plusDays(1).atStartOfDay()
        );

        Double conversionRate = totalBookings > 0
                ? (paidBookings.doubleValue() / totalBookings.doubleValue()) * 100
                : 0.0;

        List<DashboardStatsDTO.BookingStatusCount> statusDistribution = getBookingStatusDistribution();

        return DashboardStatsDTO.BookingStats.builder()
                .totalBookings(totalBookings)
                .paidBookings(paidBookings)
                .pendingConfirmation(pendingConfirmation)
                .pendingPayment(pendingPayment)
                .pendingRefund(pendingRefund)
                .cancelledBookings(cancelledBookings)
                .todayBookings(todayBookings)
                .thisWeekBookings(thisWeekBookings)
                .conversionRate(conversionRate)
                .statusDistribution(statusDistribution)
                .build();
    }

    // ========================================================================
    // HELPER METHODS: TOUR STATISTICS
    // ========================================================================
    private DashboardStatsDTO.TourStats getTourStatistics() {
        Long totalTours = tourRepository.count();
        Long activeTours = tourRepository.countByStatus(true);
        Long totalDepartures = tourRepository.countAllDepartures();
        Long upcomingDepartures = tourRepository.countUpcomingDepartures(LocalDateTime.now());

        List<DashboardStatsDTO.HotTour> hotTours = getHotTours(5);
        List<DashboardStatsDTO.TourNeedingAttention> toursNeedingAttention = getToursNeedingAttention();
        Double averageRating = reviewRepository.calculateAverageRating();

        return DashboardStatsDTO.TourStats.builder()
                .totalTours(totalTours)
                .activeTours(activeTours)
                .totalDepartures(totalDepartures)
                .upcomingDepartures(upcomingDepartures)
                .hotTours(hotTours)
                .toursNeedingAttention(toursNeedingAttention)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .build();
    }

    // ========================================================================
    // [UPDATED] HELPER METHODS: RECENT ACTIVITIES (LỌC USER LÀ CUSTOMER)
    // ========================================================================
    private List<DashboardStatsDTO.RecentActivity> getRecentActivities() {
        List<DashboardStatsDTO.RecentActivity> activities = new ArrayList<>();

        // Booking cần xác nhận
        List<Booking> pendingBookings = bookingRepository
                .findTop5ByBookingStatusOrderByCreatedAtDesc(BookingStatus.PENDING_CONFIRMATION);
        for (Booking booking : pendingBookings) {
            activities.add(DashboardStatsDTO.RecentActivity.builder()
                    .type("BOOKING")
                    .description("Booking " + booking.getBookingCode() + " chờ xác nhận")
                    .timestamp(booking.getCreatedAt().toLocalDate())
                    .severity("WARNING")
                    .relatedCode(booking.getBookingCode())
                    .build());
        }

        // Yêu cầu hoàn tiền
        List<Booking> refundRequests = bookingRepository
                .findTop5ByBookingStatusOrderByCreatedAtDesc(BookingStatus.PENDING_REFUND);
        for (Booking booking : refundRequests) {
            activities.add(DashboardStatsDTO.RecentActivity.builder()
                    .type("REFUND")
                    .description("Yêu cầu hoàn tiền: " + booking.getBookingCode())
                    .timestamp(booking.getCreatedAt().toLocalDate())
                    .severity("URGENT")
                    .relatedCode(booking.getBookingCode())
                    .build());
        }

        // [UPDATED] User mới - Chỉ lấy Role.CUSTOMER
        List<User> newUsers = userRepository.findTop5ByRoleOrderByCreatedAtDesc(Role.CUSTOMER);
        for (User user : newUsers) {
            activities.add(DashboardStatsDTO.RecentActivity.builder()
                    .type("USER")
                    .description("Khách hàng mới: " + user.getFullName())
                    .timestamp(user.getCreatedAt().toLocalDate())
                    .severity("INFO")
                    .relatedCode(user.getEmail())
                    .build());
        }

        return activities.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(10)
                .collect(Collectors.toList());
    }

    // ========================================================================
    // [UPDATED] HELPER METHODS: UTILS & DATA FETCHING
    // ========================================================================
    private DashboardStatsDTO.ChartsData getChartsData() {
        return DashboardStatsDTO.ChartsData.builder()
                .revenueChart(getDailyRevenue(30))
                .userGrowthChart(getDailyUserGrowth(30)) // Hàm này đã được update bên dưới
                .bookingStatusChart(getBookingStatusDistribution())
                .tourPerformanceChart(getTourPerformanceChart(10))
                .build();
    }

    private Double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        BigDecimal safeCurrent = current != null ? current : BigDecimal.ZERO;
        BigDecimal safePrevious = previous != null ? previous : BigDecimal.ZERO;

        if (safePrevious.compareTo(BigDecimal.ZERO) == 0) {
            return safeCurrent.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }

        return safeCurrent.subtract(safePrevious)
                .divide(safePrevious, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private Double calculateGrowthRate(Long current, Long previous) {
        long safeCurrent = current != null ? current : 0L;
        long safePrevious = previous != null ? previous : 0L;

        if (safePrevious == 0) {
            return safeCurrent > 0 ? 100.0 : 0.0;
        }
        return ((double)(safeCurrent - safePrevious) / safePrevious) * 100;
    }

    // [UPDATED] Hàm lấy biểu đồ tăng trưởng User - Truyền Role.CUSTOMER
    private List<DashboardStatsDTO.DailyUserGrowth> getDailyUserGrowth(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        // Query biểu đồ theo CUSTOMER
        List<DashboardStatsDTO.DailyUserGrowth> dailyGrowth = userRepository.getDailyNewUsers(
                startDateTime, endDateTime, Role.CUSTOMER
        );

        // Query tổng tích lũy trước đó cũng theo CUSTOMER
        Long currentTotalUsers = userRepository.countByRoleAndCreatedAtBefore(Role.CUSTOMER, startDateTime);

        for (DashboardStatsDTO.DailyUserGrowth dayStat : dailyGrowth) {
            currentTotalUsers += dayStat.getNewUsers();
            dayStat.setTotalUsers(currentTotalUsers);
        }
        return dailyGrowth;
    }

    private List<DashboardStatsDTO.DailyRevenue> getDailyRevenue(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return bookingRepository.getDailyRevenue(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                BookingStatus.PAID
        );
    }

    private Map<String, BigDecimal> getRevenueByTour(int limit) {
        return bookingRepository.getRevenueByTour(BookingStatus.PAID, limit);
    }

    private List<DashboardStatsDTO.BookingStatusCount> getBookingStatusDistribution() {
        return bookingRepository.getBookingStatusDistribution();
    }

    private List<DashboardStatsDTO.HotTour> getHotTours(int limit) {
        return tourRepository.getHotTours(BookingStatus.PAID, limit);
    }

    private List<DashboardStatsDTO.TourNeedingAttention> getToursNeedingAttention() {
        List<DashboardStatsDTO.TourNeedingAttention> result = new ArrayList<>();

        List<Tour> toursWithRefunds = tourRepository.getToursWithMostRefunds(5);
        for (Tour tour : toursWithRefunds) {
            result.add(DashboardStatsDTO.TourNeedingAttention.builder()
                    .tourId(tour.getTourID())
                    .tourCode(tour.getTourCode())
                    .tourName(tour.getTourName())
                    .reason("REFUND_REQUEST")
                    .urgency("HIGH")
                    .build());
        }

        List<Tour> lowBookingTours = tourRepository.getToursWithLowBookings(5);
        for (Tour tour : lowBookingTours) {
            result.add(DashboardStatsDTO.TourNeedingAttention.builder()
                    .tourId(tour.getTourID())
                    .tourCode(tour.getTourCode())
                    .tourName(tour.getTourName())
                    .reason("LOW_BOOKING")
                    .urgency("MEDIUM")
                    .build());
        }
        return result;
    }

    private List<DashboardStatsDTO.TourPerformance> getTourPerformanceChart(int limit) {
        return tourRepository.getTourPerformance(BookingStatus.PAID, limit);
    }
}