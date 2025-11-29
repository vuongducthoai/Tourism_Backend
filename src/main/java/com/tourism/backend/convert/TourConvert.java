package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.TourDepartureDateResponseDTO; // 👈 Import DTO mới
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.DeparturePricing;
import com.tourism.backend.entity.DepartureTransport; // 👈 Import DepartureTransport
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourDeparture;
import com.tourism.backend.entity.TourImage;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.enums.TransportType;
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
     * Chuyển đổi Entity Tour sang TourResponseDTO (Aggregated DTO).
     */
    public TourResponseDTO convertToTourReponsetoryDTO(Tour tour) {
        TourResponseDTO dto = modelMapper.map(tour, TourResponseDTO.class);
        dto.setStartPointName(tour.getStartLocation().getName());

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

        // 4. Lấy Danh sách Ngày Khởi Hành (departureDates) & Giá Thấp Nhất (money)
        if (tour.getDepartures() != null && !tour.getDepartures().isEmpty()) {

            // a) Lấy Danh sách Ngày Khởi Hành (List<TourDepartureDateResponseDTO>)
            List<TourDepartureDateResponseDTO> departureDates = tour.getDepartures().stream()
                    .map(departure -> {

                        // LỌC CHỈ LẤY CHUYẾN "OUTBOUND" (Chiều đi)
                        Optional<DepartureTransport> outboundTransportOpt = departure.getTransports().stream()
                                .filter(t -> t.getType() == TransportType.OUTBOUND) // 👈 Chỉ lấy OUTBOUND
                                .min((t1, t2) -> t1.getDepartTime().compareTo(t2.getDepartTime()));

                        LocalDate departDate = outboundTransportOpt
                                .map(t -> t.getDepartTime().toLocalDate())
                                .orElse(null);

                        return TourDepartureDateResponseDTO.builder()
                                .departureID(departure.getDepartureID())
                                .departureDate(departDate)
                                .build();
                    })
                    .filter(d -> d.getDepartureDate() != null)
                    .sorted((d1, d2) -> d1.getDepartureDate().compareTo(d2.getDepartureDate()))
                    .collect(Collectors.toList());

            dto.setDepartureDates(departureDates);

// b) Lấy Giá Thấp Nhất (originalPrice của ADULT thấp nhất trong tất cả các DeparturePricing)
            Long minPrice = tour.getDepartures().stream()
                    .flatMap(departure -> departure.getPricings().stream())
                    .filter(p -> p.getPassengerType() == PassengerType.ADULT) // <-- Dùng Enum PassengerType.ADULT
                    // Map sang originalPrice (BigDecimal)
                    .map(DeparturePricing::getOriginalPrice)
                    // Tìm giá trị nhỏ nhất (min)
                    .min(BigDecimal::compareTo)
                    // Chuyển BigDecimal sang Long (hoặc 0L nếu không tìm thấy)
                    .map(BigDecimal::longValue)
                    .orElse(0L);
            dto.setMoney(minPrice); // <-- Gán giá trị minPrice đã tìm được
        } else {
            dto.setMoney(0L);
            dto.setDepartureDates(List.of());
        }
        return dto;
    }
}