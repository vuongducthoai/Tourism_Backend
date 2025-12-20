package com.tourism.backend.service.chatbot;

import com.google.gson.Gson;
import com.tourism.backend.dto.chatbot.VectorDocumentDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.enums.TransportType;
import com.tourism.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorSyncService {

    private final VectorService vectorService;
    private final TourRepository tourRepository;
    private final LocationRepository locationRepository;
    private final ReviewRepository reviewRepository;
    private final CouponRepository couponRepository;
    private final Gson gson = new Gson();

    @Transactional(readOnly = true)
    public void syncAllTours() {
        log.info("🔄 Starting comprehensive tour sync...");

        List<Tour> tours = tourRepository.findAll();
        int tourCount = 0;
        int departureCount = 0;
        LocalDate today = LocalDate.now();

        for (Tour tour : tours) {
            try {
                // ✅ CHỈ SYNC TOUR NẾU CÓ ÍT NHẤT 1 DEPARTURE CÒN HOẠT ĐỘNG
                boolean hasActiveDeparture = tour.getDepartures() != null &&
                        tour.getDepartures().stream()
                                .anyMatch(dep -> {
                                    LocalDate depDate = getDepartureDate(dep);
                                    return depDate != null && depDate.isAfter(today) &&
                                            Boolean.TRUE.equals(dep.getStatus());
                                });

                if (!hasActiveDeparture) {
                    log.debug("⏭️ Skipping tour {} - no active departures", tour.getTourCode());
                    continue;
                }

                syncTourSummary(tour);
                tourCount++;

                if (tour.getDepartures() != null) {
                    for (TourDeparture departure : tour.getDepartures()) {
                        // ✅ CHỈ SYNC DEPARTURE CÓ NGÀY KHỞI HÀNH TRONG TƯƠNG LAI
                        LocalDate depDate = getDepartureDate(departure);
                        if (depDate != null && depDate.isAfter(today) && Boolean.TRUE.equals(departure.getStatus())) {
                            syncTourDeparture(tour, departure);
                            departureCount++;
                        }
                    }
                }

            } catch (Exception e) {
                log.error("❌ Error syncing tour: {}", tour.getTourCode(), e);
            }
        }

        log.info("✅ Synced {} tours and {} departures to vector DB", tourCount, departureCount);
    }

    public void syncTourSummary(Tour tour) {
        StringBuilder content = new StringBuilder();

        content.append("Tour: ").append(tour.getTourName()).append(". ");
        content.append("Mã tour: ").append(tour.getTourCode()).append(". ");
        content.append("Thời gian: ").append(tour.getDuration()).append(". ");
        content.append("Điểm khởi hành: ").append(tour.getStartLocation().getName()).append(". ");
        content.append("Điểm đến: ").append(tour.getEndLocation().getName()).append(". ");

        if (tour.getAttractions() != null) {
            content.append("Điểm tham quan: ").append(tour.getAttractions()).append(". ");
        }

        if (tour.getMeals() != null) {
            content.append("Bữa ăn: ").append(tour.getMeals()).append(". ");
        }

        if (tour.getHotel() != null) {
            content.append("Khách sạn: ").append(tour.getHotel()).append(". ");
        }

        content.append("Phương tiện: ").append(tour.getTransportation()).append(". ");

        addReviewInfo(content, tour);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tourCode", tour.getTourCode());
        metadata.put("tourName", tour.getTourName());
        metadata.put("duration", tour.getDuration());
        metadata.put("startLocation", tour.getStartLocation().getName());
        metadata.put("endLocation", tour.getEndLocation().getName());
        metadata.put("transportation", tour.getTransportation());

        String imageUrl = tour.getImages().stream()
                .filter(TourImage::getIsMainImage)
                .findFirst()
                .map(TourImage::getImageURL)
                .orElse(null);
        metadata.put("imageUrl", imageUrl);

        List<Float> embedding = vectorService.createEmbedding(content.toString());

        VectorDocumentDTO document = VectorDocumentDTO.builder()
                .id("tour_summary_" + tour.getTourID())
                .content(content.toString())
                .type("TOUR_SUMMARY")
                .entityId(tour.getTourID())
                .embedding(embedding)
                .metadata(gson.toJson(metadata))
                .build();

        vectorService.upsertVector(document);
        log.info("✅ Synced tour summary: {}", tour.getTourCode());
    }

    public void syncTourDeparture(Tour tour, TourDeparture departure) {
        LocalDate today = LocalDate.now();
        LocalDate departDate = getDepartureDate(departure);

        if (departDate == null || !departDate.isAfter(today) || !Boolean.TRUE.equals(departure.getStatus())) {
            return;
        }

        StringBuilder content = new StringBuilder();

        content.append("Tour: ").append(tour.getTourName()).append(" (").append(tour.getTourCode()).append("). ");
        content.append("Ngày khởi hành: ").append(departDate).append(". ");
        content.append("Thời gian: ").append(tour.getDuration()).append(". ");
        content.append("Điểm đến: ").append(tour.getEndLocation().getName()).append("). ");

        DeparturePricing adultPricing = departure.getPricings().stream()
                .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                .findFirst()
                .orElse(null);

        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal couponDiscount = BigDecimal.ZERO;

        if (adultPricing != null) {
            BigDecimal salePrice = adultPricing.getSalePrice();
            BigDecimal originalPrice = adultPricing.getOriginalPrice();
            BigDecimal discount = originalPrice.subtract(salePrice);

            content.append("Giá người lớn: ").append(String.format("%,.0f", salePrice)).append(" VND. ");

            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                content.append("Giá gốc: ").append(String.format("%,.0f", originalPrice)).append(" VND. ");
                content.append("Giảm: ").append(String.format("%,.0f", discount)).append(" VND. ");
                totalDiscount = discount;
            }
        }

        // ✅ Thêm thông tin coupon vào content VÀ lưu vào metadata
        // ✅ CHỈ LẤY COUPON CÒN HẠN: startDate <= now <= endDate
        if (departure.getCoupon() != null && departure.getCoupon().isValid()) {
            Coupon coupon = departure.getCoupon();
            LocalDateTime now = LocalDateTime.now();

            // ✅ KIỂM TRA COUPON ĐÃ ĐẾN NGÀY SỬ DỤNG VÀ CHƯA HẾT HẠN
            boolean isWithinValidPeriod =
                    (coupon.getStartDate() == null || now.isAfter(coupon.getStartDate()) || now.isEqual(coupon.getStartDate())) &&
                            (coupon.getEndDate() == null || now.isBefore(coupon.getEndDate()));

            if (isWithinValidPeriod) {
                // ✅ CHUYỂN ĐỔI Integer -> BigDecimal
                couponDiscount = BigDecimal.valueOf(coupon.getDiscountAmount());
                totalDiscount = totalDiscount.add(couponDiscount);

                content.append("Mã khuyến mãi đặc biệt: ").append(coupon.getCouponCode())
                        .append(" - Giảm thêm ").append(String.format("%,.0f", couponDiscount)).append(" VND. ");

                if (coupon.getStartDate() != null) {
                    content.append("Có hiệu lực từ: ").append(coupon.getStartDate().toLocalDate()).append(". ");
                }
                if (coupon.getEndDate() != null) {
                    content.append("Hết hạn: ").append(coupon.getEndDate().toLocalDate()).append(". ");
                }
            }
        }

        if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            content.append("Tổng mức giảm: ").append(String.format("%,.0f", totalDiscount)).append(" VND. ");
        }

        content.append("Còn ").append(departure.getAvailableSlots()).append(" chỗ trống. ");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("departureID", departure.getDepartureID());
        metadata.put("tourID", tour.getTourID());
        metadata.put("tourCode", tour.getTourCode());
        metadata.put("tourName", tour.getTourName());
        metadata.put("departureDate", departDate.toString());
        metadata.put("availableSlots", departure.getAvailableSlots());

        if (adultPricing != null) {
            metadata.put("salePrice", adultPricing.getSalePrice().doubleValue());
            metadata.put("originalPrice", adultPricing.getOriginalPrice().doubleValue());
            metadata.put("discount", adultPricing.getOriginalPrice().subtract(adultPricing.getSalePrice()).doubleValue());
        }

        // ✅ LƯU COUPON DISCOUNT VÀ THÔNG TIN THỜI HAN VÀO METADATA
        if (couponDiscount.compareTo(BigDecimal.ZERO) > 0) {
            metadata.put("couponDiscount", couponDiscount.doubleValue());
            metadata.put("totalDiscount", totalDiscount.doubleValue());

            Coupon coupon = departure.getCoupon();
            if (coupon != null) {
                metadata.put("couponCode", coupon.getCouponCode());
                if (coupon.getStartDate() != null) {
                    metadata.put("couponStartDate", coupon.getStartDate().toString());
                }
                if (coupon.getEndDate() != null) {
                    metadata.put("couponEndDate", coupon.getEndDate().toString());
                }
            }
        }

        List<Float> embedding = vectorService.createEmbedding(content.toString());

        VectorDocumentDTO document = VectorDocumentDTO.builder()
                .id("departure_" + departure.getDepartureID())
                .content(content.toString())
                .type("TOUR_DEPARTURE")
                .entityId(departure.getDepartureID())
                .embedding(embedding)
                .metadata(gson.toJson(metadata))
                .build();

        vectorService.upsertVector(document);
    }    private void addReviewInfo(StringBuilder content, Tour tour) {
        List<Review> reviews = reviewRepository.findByTourTourID(tour.getTourID());

        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            content.append("Đánh giá trung bình: ").append(String.format("%.1f", avgRating)).append("/5 sao. ");
            content.append("Số lượng đánh giá: ").append(reviews.size()).append(". ");

            String topReviews = reviews.stream()
                    .filter(r -> r.getRating() >= 4)
                    .limit(2)
                    .map(Review::getComment)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(". "));

            if (!topReviews.isEmpty()) {
                content.append("Khách hàng nhận xét: ").append(topReviews).append(". ");
            }
        }
    }

    private void addCouponInfo(StringBuilder content, TourDeparture departure) {
        if (departure.getCoupon() != null && departure.getCoupon().isValid()) {
            Coupon coupon = departure.getCoupon();
            content.append("Mã khuyến mãi đặc biệt: ").append(coupon.getCouponCode())
                    .append(" - Giảm ").append(coupon.getDiscountAmount()).append(" VND. ");
        }
    }

    private LocalDate getDepartureDate(TourDeparture departure) {
        if (departure.getTransports() == null || departure.getTransports().isEmpty()) {
            return null;
        }

        return departure.getTransports().stream()
                .filter(t -> t.getType() == TransportType.OUTBOUND)
                .min(Comparator.comparing(DepartureTransport::getDepartTime))
                .map(t -> t.getDepartTime().toLocalDate())
                .orElse(null);
    }

    /**
     * ✅ SYNC LOCATIONS - THÊM LOCATIONID VÀO METADATA VÀ CONTENT
     */
    @Transactional(readOnly = true)
    public void syncAllLocations() {
        log.info("🔄 Starting location sync...");

        List<Location> locations = locationRepository.findLocationsWithActiveTours();
        int count = 0;
        LocalDate today = LocalDate.now();

        for (Location location : locations) {
            try {
                // ✅ CHỈ SYNC LOCATION NẾU CÓ ÍT NHẤT 1 TOUR CÓ DEPARTURE CÒN HOẠT ĐỘNG
                boolean hasActiveTourWithDeparture = false;

                // Kiểm tra tours có điểm đến là location này
                if (location.getEndPoint() != null) {
                    hasActiveTourWithDeparture = location.getEndPoint().stream()
                            .filter(tour -> Boolean.TRUE.equals(tour.getStatus()))
                            .anyMatch(tour -> tour.getDepartures() != null &&
                                    tour.getDepartures().stream()
                                            .anyMatch(dep -> {
                                                LocalDate depDate = getDepartureDate(dep);
                                                return depDate != null && depDate.isAfter(today) &&
                                                        Boolean.TRUE.equals(dep.getStatus());
                                            }));
                }

                if (!hasActiveTourWithDeparture) {
                    log.debug("⏭️ Skipping location {} - no tours with active departures", location.getName());
                    continue;
                }

                String content = buildLocationContent(location);

                // ✅ THÊM LOCATIONID VÀO METADATA
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("locationID", location.getLocationID()); // ✅ QUAN TRỌNG
                metadata.put("locationName", location.getName());
                metadata.put("region", location.getRegion().name());
                metadata.put("airportCode", location.getAirportCode());

                List<Float> embedding = vectorService.createEmbedding(content);

                VectorDocumentDTO document = VectorDocumentDTO.builder()
                        .id("location_" + location.getLocationID())
                        .content(content)
                        .type("LOCATION")
                        .entityId(location.getLocationID()) // ✅ entityId = locationID
                        .embedding(embedding)
                        .metadata(gson.toJson(metadata))
                        .build();

                vectorService.upsertVector(document);
                count++;

            } catch (Exception e) {
                log.error("❌ Error syncing location: {}", location.getName(), e);
            }
        }

        log.info("✅ Synced {} locations to vector DB", count);
    }

    /**
     * ✅ BUILD LOCATION CONTENT - THÊM LOCATIONID VÀO NỘI DUNG
     */
    private String buildLocationContent(Location location) {
        StringBuilder content = new StringBuilder();

        // ✅ THÊM LOCATIONID VÀO CONTENT ĐỂ AI NHÌN THẤY
        content.append("Địa điểm: ").append(location.getName())
                .append(" (ID: ").append(location.getLocationID()).append("). ");

        content.append("Vùng miền: ").append(location.getRegion()).append(". ");

        if (location.getDescription() != null) {
            content.append("Mô tả: ").append(location.getDescription()).append(". ");
        }

        if (location.getAirportCode() != null) {
            content.append("Sân bay: ").append(location.getAirportName())
                    .append(" (").append(location.getAirportCode()).append("). ");
        }

        // ✅ ĐẾM SỐ TOURS ĐẾN ĐỊA ĐIỂM NÀY
        int tourCount = location.getEndPoint() != null ? location.getEndPoint().size() : 0;
        if (tourCount > 0) {
            content.append("Có ").append(tourCount).append(" tour đến đây. ");
        }

        return content.toString();
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledSync() {
        log.info("🕐 Running scheduled vector sync...");
        syncAllTours();
        syncAllLocations();
    }
}