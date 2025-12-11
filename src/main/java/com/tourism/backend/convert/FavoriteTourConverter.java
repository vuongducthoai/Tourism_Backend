package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.TourDepartureDateResponseDTO;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.DeparturePricing;
import com.tourism.backend.entity.DepartureTransport;
import com.tourism.backend.entity.Tour;
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
public class FavoriteTourConverter {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert Tour entity sang TourResponseDTO với trạng thái favorite
     * @param tour Tour entity cần convert
     * @param isFavorite Đánh dấu tour có phải là favorite không
     * @return TourResponseDTO
     */
    public TourResponseDTO convertToTourResponseDTO(Tour tour, boolean isFavorite) {
        TourResponseDTO dto = modelMapper.map(tour, TourResponseDTO.class);

        // Set start point name
        dto.setStartPointName(tour.getStartLocation().getName());

        // Set favorite status
        dto.setIsFavorite(isFavorite);

        // Set main image
        setMainImage(dto, tour);

        LocalDate today = LocalDate.now();

        // Set departure dates and min price
        if (tour.getDepartures() != null && !tour.getDepartures().isEmpty()) {
            setDepartureDates(dto, tour, today);
            setMinPrice(dto, tour, today);
        } else {
            dto.setMoney(0L);
            dto.setDepartureDates(List.of());
        }

        return dto;
    }

    /**
     * Set ảnh chính cho tour
     */
    private void setMainImage(TourResponseDTO dto, Tour tour) {
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
    }

    /**
     * Set danh sách ngày khởi hành (chỉ những ngày trong tương lai)
     */
    private void setDepartureDates(TourResponseDTO dto, Tour tour, LocalDate today) {
        List<TourDepartureDateResponseDTO> departureDates = tour.getDepartures().stream()
                // Lọc departure có status = true
                .filter(departure -> departure.getStatus() != null && departure.getStatus())
                // Lấy OUTBOUND transport và ngày khởi hành
                .map(departure -> {
                    Optional<DepartureTransport> outboundTransportOpt = getOutboundTransport(departure);

                    LocalDate departDate = outboundTransportOpt
                            .map(t -> t.getDepartTime().toLocalDate())
                            .orElse(null);

                    return TourDepartureDateResponseDTO.builder()
                            .departureID(departure.getDepartureID())
                            .departureDate(departDate)
                            .build();
                })
                // Lọc chỉ lấy những departure có ngày trong tương lai
                .filter(d -> d.getDepartureDate() != null && d.getDepartureDate().isAfter(today))
                // Sắp xếp theo ngày tăng dần
                .sorted((d1, d2) -> d1.getDepartureDate().compareTo(d2.getDepartureDate()))
                .collect(Collectors.toList());

        dto.setDepartureDates(departureDates);
    }

    /**
     * Set giá thấp nhất (từ các departure hợp lệ trong tương lai)
     */
    private void setMinPrice(TourResponseDTO dto, Tour tour, LocalDate today) {
        Long minPrice = tour.getDepartures().stream()
                // Lọc departure có status = true
                .filter(departure -> departure.getStatus() != null && departure.getStatus())
                // Kiểm tra có OUTBOUND transport và ngày trong tương lai
                .filter(departure -> isValidFutureDeparture(departure, today))
                // Lấy tất cả pricings
                .flatMap(departure -> departure.getPricings() != null
                        ? departure.getPricings().stream()
                        : java.util.stream.Stream.empty())
                // Chỉ lấy giá của ADULT
                .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                // Lấy originalPrice
                .map(DeparturePricing::getOriginalPrice)
                // Tìm giá thấp nhất
                .min(BigDecimal::compareTo)
                .map(BigDecimal::longValue)
                .orElse(0L);

        dto.setMoney(minPrice);
    }

    /**
     * Lấy OUTBOUND transport (chiều đi) sớm nhất
     */
    private Optional<DepartureTransport> getOutboundTransport(com.tourism.backend.entity.TourDeparture departure) {
        if (departure.getTransports() == null) {
            return Optional.empty();
        }

        return departure.getTransports().stream()
                .filter(t -> t.getType() == TransportType.OUTBOUND)
                .min((t1, t2) -> t1.getDepartTime().compareTo(t2.getDepartTime()));
    }

    /**
     * Kiểm tra departure có hợp lệ và trong tương lai không
     */
    private boolean isValidFutureDeparture(com.tourism.backend.entity.TourDeparture departure, LocalDate today) {
        if (departure.getTransports() == null) {
            return false;
        }

        Optional<DepartureTransport> outboundTransportOpt = getOutboundTransport(departure);

        if (outboundTransportOpt.isEmpty()) {
            return false;
        }

        LocalDate departDate = outboundTransportOpt.get().getDepartTime().toLocalDate();
        return departDate.isAfter(today);
    }
}