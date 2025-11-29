package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.TourSpecialResponseDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.enums.TransportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TourSpecialConvert {
    // 1. CHUYỂN THÀNH PUBLIC STATIC CLASS ĐỂ SERVICE CÓ THỂ TRUY CẬP
    public static class DiscountInfo {
        public final TourDeparture departure; // Đổi thành public final
        public final BigDecimal discountValue; // Đổi thành public final

        // Constructor giữ nguyên logic tính toán
        public DiscountInfo(TourDeparture departure) {
            this.departure = departure;
            this.discountValue = calculateDiscount(departure);
        }

        private BigDecimal calculateDiscount(TourDeparture dep) {
            // Tìm giá ADULT
            DeparturePricing adultPrice = dep.getPricings().stream()
                    .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                    .findFirst()
                    .orElse(null);

            if (adultPrice != null && adultPrice.getOriginalPrice() != null && adultPrice.getSalePrice() != null) {
                // originalPrice - salePrice
                return adultPrice.getOriginalPrice().subtract(adultPrice.getSalePrice());
            }
            return BigDecimal.ZERO;
        }
    }

    public TourSpecialResponseDTO mapToTourSpecialResponseDTO(DiscountInfo info) {
        TourDeparture dep = info.departure;
        Tour tour = dep.getTour();

        // Lấy giá ADULT chính xác (giữ nguyên)
        DeparturePricing adultPrice = dep.getPricings().stream()
                .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                .findFirst()
                .orElse(new DeparturePricing());

        // Lấy departTime (OUTBOUND) (giữ nguyên)
        DepartureTransport outboundTransport = dep.getTransports().stream()
                .filter(t -> t.getType() == TransportType.OUTBOUND)
                .findFirst()
                .orElse(null);

        // Tính giá trị giảm tuyệt đối (giữ nguyên)
        BigDecimal discountValue = info.discountValue;

        // ✨ LOGIC LẤY ẢNH ĐẦU TIÊN ✨
        String imageUrl = null;
        if (tour.getImages() != null && !tour.getImages().isEmpty()) { // <-- Hibernate sẽ tải ảnh ở đây
            // Ưu tiên lấy ảnh chính (isMainImage = TRUE)
            Optional<TourImage> mainImageOpt = tour.getImages().stream()
                    .filter(TourImage::getIsMainImage)
                    .findFirst();

            // Nếu có ảnh chính, dùng ảnh đó, nếu không, dùng ảnh đầu tiên trong list
            imageUrl = mainImageOpt.map(TourImage::getImageURL)
                    .orElse(tour.getImages().get(0).getImageURL());
        }
        // ✨ KẾT THÚC LOGIC LẤY ẢNH ✨


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
                .salePrice(adultPrice.getSalePrice())
                .originalPrice(adultPrice.getOriginalPrice())
                .discountPercentage(discountValue)
                .image(imageUrl) // <-- Gán URL ảnh vào trường image mới
                .build();
    }
}