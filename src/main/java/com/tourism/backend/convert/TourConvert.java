
package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.DeparturePricing;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.entity.TourImage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TourConvert {
    @Autowired
    ModelMapper modelMapper;

    /**
     * Chuyển đổi Entity Tour sang TourReponsetory DTO (Aggregated DTO).
     */
    public TourResponseDTO convertToTourReponsetoryDTO(Tour tour) {
        TourResponseDTO dto = modelMapper.map(tour, TourResponseDTO.class);
        dto.setEndPointName(tour.getEndLocation().getName());
        if (tour.getImages() != null && !tour.getImages().isEmpty()) {
            Optional<TourImage> mainImageOpt = tour.getImages().stream()
                    .filter(TourImage::getIsMainImage)
                    .findFirst();
            String imageUrl = mainImageOpt.isPresent()
                    ? mainImageOpt.get().getImageURL()
                    : tour.getImages().get(0).getImageURL();

            dto.setImage(imageUrl);
        } else {
            dto.setImage(null);
        }

        // 4. Lấy Danh sách Ngày Khởi Hành (departureDate) & Giá Thấp Nhất (money)
        if (tour.getDepartures() != null && !tour.getDepartures().isEmpty()) {
            // a) Lấy Danh sách Ngày Khởi Hành
            List<LocalDate> departureDates = tour.getDepartures().stream()
                    .map(TourDeparture::getDepartureDate)
                    .sorted()
                    .collect(Collectors.toList());

            dto.setDepartureDate(departureDates);

            // b) Lấy Giá Thấp Nhất (Giá ADULT thấp nhất trong tất cả các DeparturePricing)
            Long minPrice = tour.getDepartures().stream()
                    .flatMap(departure -> departure.getPricings().stream())
                    .filter(p -> "ADULT".equals(p.getPassengerType()))
                    .map(DeparturePricing::getMoney)
                    .min(BigDecimal::compareTo)
                    .map(BigDecimal::longValue)
                    .orElse(0L);

            dto.setMoney(minPrice);
        } else {
            dto.setMoney(0L);
            dto.setDepartureDate(List.of());
        }

        return dto;
    }
}