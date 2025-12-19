package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.CouponRequest;
import com.tourism.backend.dto.response.CouponResponse;
import com.tourism.backend.entity.Coupon;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.enums.CouponType;
import com.tourism.backend.exception.BadRequestException;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.CouponRepository;
import com.tourism.backend.repository.TourDepartureRepository;
import com.tourism.backend.service.CouponService;
import com.tourism.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final TourDepartureRepository tourDepartureRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        // 1. Kiểm tra trùng mã
        if (couponRepository.existsByCouponCode(request.getCouponCode())) {
            throw new DuplicateResourceException("Mã coupon đã tồn tại: " + request.getCouponCode());
        }

        // 2. Tạo đối tượng Coupon (Chưa gán Departure vội)
        Coupon coupon = new Coupon();
        coupon.setCouponCode(request.getCouponCode());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountAmount(request.getDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsageCount(0);
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setCouponType(request.getCouponType());

        // 3. Lưu Coupon xuống DB trước để lấy ID
        Coupon savedCoupon = couponRepository.save(coupon);

        // 4. Nếu là loại DEPARTURE, tìm các chuyến đi và gán coupon_id cho chúng
        if (request.getCouponType() == CouponType.DEPARTURE) {
            if (request.getDepartureIds() == null || request.getDepartureIds().isEmpty()) {
                throw new BadRequestException("Vui lòng chọn ít nhất một lịch khởi hành (Departure)");
            }

            List<TourDeparture> departures = tourDepartureRepository.findAllById(request.getDepartureIds());

            if (departures.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy lịch khởi hành nào hợp lệ");
            }

            // [QUAN TRỌNG] Logic 1-N: Set coupon cho nhiều departure
            for (TourDeparture dep : departures) {
                dep.setCoupon(savedCoupon);
            }
            // Lưu cập nhật lại bảng TourDeparture
            tourDepartureRepository.saveAll(departures);
        }

        // 5. Map response và gửi thông báo
        CouponResponse response = mapToResponse(savedCoupon);

        if (Boolean.TRUE.equals(request.getSendNotification())) {
            notifyNewCoupon(response, "NEW_COUPON");
        }

        log.info("Created new coupon: {}", savedCoupon.getCouponCode());
        return response;
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Integer couponId, CouponRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        coupon.setDescription(request.getDescription());
        coupon.setDiscountAmount(request.getDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setCouponType(request.getCouponType());

        Coupon updatedCoupon = couponRepository.save(coupon);

        tourDepartureRepository.removeCouponFromDepartures(couponId);

        if (request.getCouponType() == CouponType.DEPARTURE) {
            if (request.getDepartureIds() != null && !request.getDepartureIds().isEmpty()) {
                List<TourDeparture> newDepartures = tourDepartureRepository.findAllById(request.getDepartureIds());
                for (TourDeparture dep : newDepartures) {
                    dep.setCoupon(updatedCoupon);
                }
                tourDepartureRepository.saveAll(newDepartures);
            }
        }

        CouponResponse response = mapToResponse(updatedCoupon);

        if (Boolean.TRUE.equals(request.getSendNotification())) {
            notifyNewCoupon(response, "COUPON_UPDATED");
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteCoupon(Integer couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy coupon"));

        coupon.setIsDeleted(true);

        tourDepartureRepository.removeCouponFromDepartures(couponId);

        couponRepository.save(coupon);
        log.info("Soft deleted coupon: {}", coupon.getCouponCode());
    }

    @Override
    public CouponResponse getCouponById(Integer couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy coupon"));
        return mapToResponse(coupon);
    }

    @Override
    public Page<CouponResponse> getAllCoupons(Pageable pageable) {
        return couponRepository.findAllSorted(pageable).map(this::mapToResponse);
    }

    @Override
    public List<CouponResponse> getActiveCoupons() {
        return couponRepository.findActiveCoupons(LocalDateTime.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<CouponResponse> getApplicableCouponsForBooking(Integer departureId) {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> applicableCoupons = new ArrayList<>(couponRepository.findActiveGlobalCoupons(now));
        if (departureId != null) {
            applicableCoupons.addAll(couponRepository.findActiveDepartureCoupons(departureId, now));
        }
        return applicableCoupons.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CouponResponse validateCouponForBooking(String couponCode, Integer departureId) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        if (!coupon.isValid()) {
            throw new BadRequestException("Coupon is not valid or has been used up");
        }

        if (coupon.getCouponType() == CouponType.DEPARTURE) {
            boolean isApplicable = false;
            List<TourDeparture> linkedDepartures = tourDepartureRepository.findByCoupon_CouponID(coupon.getCouponID());
            for(TourDeparture td : linkedDepartures) {
                if(td.getDepartureID().equals(departureId)) {
                    isApplicable = true;
                    break;
                }
            }

            if (!isApplicable) {
                throw new BadRequestException("Mã này không áp dụng cho chuyến đi hiện tại");
            }
        }
        return mapToResponse(coupon);
    }

    @Override
    public void notifyNewCoupon(CouponResponse coupon, String type) {
        String tempTitle = "";
        String tempMessage = "";
        long discountVal = coupon.getDiscountAmount() != null ? coupon.getDiscountAmount().longValue() : 0;

        switch (type) {
            case "NEW_COUPON":
                tempTitle = "🎉 Mã giảm giá mới!";
                tempMessage = String.format("Sử dụng mã %s để giảm %,dđ. %s",
                        coupon.getCouponCode(), discountVal,
                        coupon.getDescription() != null ? coupon.getDescription() : "");
                break;
            case "COUPON_UPDATED":
                tempTitle = "📝 Cập nhật mã giảm giá";
                tempMessage = String.format("Mã %s đã được cập nhật với ưu đãi mới", coupon.getCouponCode());
                break;
            case "COUPON_EXPIRING":
                tempTitle = "⏰ Mã giảm giá sắp hết hạn";
                tempMessage = String.format("Mã %s sẽ hết hạn vào %s. Nhanh tay sử dụng!",
                        coupon.getCouponCode(),
                        coupon.getEndDate() != null ? coupon.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")) : "");
                break;
            default:
                tempTitle = "Thông báo coupon";
                tempMessage = "Có cập nhật về mã giảm giá";
        }

        final String finalTitle = tempTitle;
        final String finalMessage = tempMessage;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("couponId", coupon.getCouponId());
        metadata.put("couponCode", coupon.getCouponCode());
        metadata.put("discountAmount", coupon.getDiscountAmount());
        metadata.put("couponType", coupon.getCouponType());
        if (coupon.getEndDate() != null) metadata.put("endDate", coupon.getEndDate().toString());

        if (coupon.getDepartureDetails() != null && !coupon.getDepartureDetails().isEmpty()) {
            List<Map<String, Object>> departureList = new ArrayList<>();
            for (CouponResponse.DepartureInfo info : coupon.getDepartureDetails()) {
                Map<String, Object> depData = new HashMap<>();
                depData.put("tourId", info.getTourId());
                depData.put("tourName", info.getTourName());
                depData.put("tourCode", info.getTourCode());
                depData.put("departureDate", info.getDepartureDate().toString());
                depData.put("departureId", info.getDepartureId());
                departureList.add(depData);
            }
            metadata.put("departures", departureList);
            if (!departureList.isEmpty()) metadata.put("departure", departureList.get(0));
        }

        metadata.put("action", "/tours");
        metadata.put("minOrderValue", coupon.getMinOrderValue());

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                log.info("Starting async notification for coupon: {}", coupon.getCouponCode());
                notificationService.createBroadcastNotification(type, finalTitle, finalMessage, metadata);
            } catch (Exception e) {
                log.error("Failed to send async notification", e);
            }
        });
        log.info("Sent {} notification request for coupon: {}", type, coupon.getCouponCode());
    }

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public void checkAndNotifyExpiringCoupons() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        List<Coupon> expiringCoupons = couponRepository.findExpiringCoupons(now, tomorrow);

        for (Coupon coupon : expiringCoupons) {
            CouponResponse response = mapToResponse(coupon);

            String typeText = "cho tất cả tour";
            if (coupon.getCouponType() == CouponType.DEPARTURE && response.getDepartureDetails() != null && !response.getDepartureDetails().isEmpty()) {
                String firstTourName = response.getDepartureDetails().get(0).getTourName();
                typeText = "cho tour " + firstTourName + (response.getDepartureDetails().size() > 1 ? " và các chuyến khác" : "");
            }

            String title = "⏰ Mã giảm giá sắp hết hạn!";
            String message = String.format("Mã %s %s sẽ hết hạn vào %s. Nhanh tay đặt tour!",
                    coupon.getCouponCode(),
                    typeText,
                    coupon.getEndDate().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

            notifyNewCoupon(response, "COUPON_EXPIRING");
        }

        if (!expiringCoupons.isEmpty()) {
            log.info("Processed expiring notifications for {} coupons", expiringCoupons.size());
        }
    }

    @Override
    public Page<CouponResponse> getDepartureCoupons(Pageable pageable) {
        return couponRepository.findDepartureCoupons(pageable).map(this::mapToResponse);
    }
    @Override
    public Page<CouponResponse> getGlobalCoupons(Pageable pageable) {
        return couponRepository.findGlobalCoupons(pageable).map(this::mapToResponse);
    }
    @Override
    public Page<CouponResponse> searchCoupons(String keyword, Pageable pageable) {
        return couponRepository.searchCoupons(keyword, pageable).map(this::mapToResponse);
    }

    private CouponResponse mapToResponse(Coupon coupon) {
        CouponResponse.CouponResponseBuilder builder = CouponResponse.builder()
                .couponId(coupon.getCouponID())
                .couponCode(coupon.getCouponCode())
                .description(coupon.getDescription())
                .discountAmount(coupon.getDiscountAmount())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .minOrderValue(coupon.getMinOrderValue())
                .isActive(coupon.isValid())
                .couponType(coupon.getCouponType() != null ? coupon.getCouponType().toString() : "GLOBAL")
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt());

        if (coupon.getCouponType() == CouponType.DEPARTURE) {
            List<TourDeparture> departures = tourDepartureRepository.findByCoupon_CouponID(coupon.getCouponID());

            List<CouponResponse.DepartureInfo> details = departures.stream()
                    .map(dep -> CouponResponse.DepartureInfo.builder()
                            .departureId(dep.getDepartureID())
                            .tourId(dep.getTour().getTourID())
                            .tourCode(dep.getTour().getTourCode())
                            .tourName(dep.getTour().getTourName())
                            .departureDate(dep.getDepartureDate())
                            .build())
                    .collect(Collectors.toList());

            builder.departureDetails(details);
        }

        return builder.build();
    }
}