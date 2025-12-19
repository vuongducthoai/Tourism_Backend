package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.TourSpecialResponseDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.enums.TransportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TourSpecialConvert {

    //  STATIC CLASS - Chỉ lưu departure và discountAmount
    public static class DiscountInfo {
        public final TourDeparture departure;
        public final Integer discountAmount; //  Lấy trực tiếp từ Coupon

        public DiscountInfo(TourDeparture departure, LocalDateTime now) {
            this.departure = departure;

            //  Kiểm tra coupon còn hạn sử dụng
            Coupon coupon = departure.getCoupon();
            if (coupon != null && isCouponValid(coupon, now)) {
                this.discountAmount = coupon.getDiscountAmount();
            } else {
                this.discountAmount = 0;
            }
        }
    }

    public TourSpecialResponseDTO mapToTourSpecialResponseDTO(DiscountInfo info) {
        TourDeparture dep = info.departure;
        Tour tour = dep.getTour();

        // Lấy giá ADULT
        DeparturePricing adultPrice = dep.getPricings().stream()
                .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                .findFirst()
                .orElse(new DeparturePricing());

        // Lấy departTime (OUTBOUND)
        DepartureTransport outboundTransport = dep.getTransports().stream()
                .filter(t -> t.getType() == TransportType.OUTBOUND)
                .findFirst()
                .orElse(null);

        // ✅ Lấy ảnh
        String imageUrl = null;
        if (tour.getImages() != null && !tour.getImages().isEmpty()) {
            Optional<TourImage> mainImageOpt = tour.getImages().stream()
                    .filter(TourImage::getIsMainImage)
                    .findFirst();

            imageUrl = mainImageOpt.map(TourImage::getImageURL)
                    .orElse(tour.getImages().get(0).getImageURL());
        }

        // ✅ Tính discountPercentage (nếu cần)
        BigDecimal discountPercentage = BigDecimal.ZERO;
        if (adultPrice.getOriginalPrice() != null && info.discountAmount > 0) {
            BigDecimal discountBD = BigDecimal.valueOf(info.discountAmount);
            discountPercentage = discountBD
                    .divide(adultPrice.getOriginalPrice(), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return TourSpecialResponseDTO.builder()
                .departureID(dep.getDepartureID())
                .tourID(tour.getTourID())
                .tourName(tour.getTourName())
                .tourCode(tour.getTourCode())
                .startLocationName(tour.getStartLocation() != null ? tour.getStartLocation().getName() : "N/A")
                .duration(tour.getDuration())
                .departureDate(outboundTransport != null && outboundTransport.getDepartTime() != null
                        ? outboundTransport.getDepartTime().toLocalDate() : null)
                .availableSlots(dep.getAvailableSlots())
                .salePrice(BigDecimal.valueOf(info.discountAmount)) // ✅ Convert Integer → BigDecimal
                .originalPrice(adultPrice.getOriginalPrice())
                .discountPercentage(discountPercentage)
                .image(imageUrl)
                .build();
    }


    private static boolean isCouponValid(Coupon coupon, LocalDateTime now) {
        LocalDateTime startDate = coupon.getStartDate();
        LocalDateTime endDate = coupon.getEndDate();

        // Kiểm tra coupon đã bắt đầu và chưa hết hạn
        boolean isStarted = (startDate == null || now.isAfter(startDate) || now.isEqual(startDate));
        boolean notExpired = (endDate == null || now.isBefore(endDate) || now.isEqual(endDate));

        return isStarted && notExpired;
    }
}