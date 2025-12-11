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
import com.tourism.backend.service.FavoriteTourService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TourConvert {
    @Autowired
    ModelMapper modelMapper;


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
        LocalDate today = LocalDate.now();
        // 4. Lấy Danh sách Ngày Khởi Hành (departureDates) & Giá Thấp Nhất (money)
        if (tour.getDepartures() != null && !tour.getDepartures().isEmpty()) {

            List<TourDepartureDateResponseDTO> departureDates = tour.getDepartures().stream()
                    // Bước 1: Lọc departure có status = true
                    .filter(departure -> departure.getStatus() != null && departure.getStatus())
                    // Bước 2: Lấy OUTBOUND transport và ngày khởi hành
                    .map(departure -> {
                        // Tìm transport OUTBOUND (chiều đi) - lấy transport có departTime sớm nhất nếu có nhiều
                        Optional<DepartureTransport> outboundTransportOpt = departure.getTransports() != null
                                ? departure.getTransports().stream()
                                .filter(t -> t.getType() == TransportType.OUTBOUND)
                                .min((t1, t2) -> t1.getDepartTime().compareTo(t2.getDepartTime()))
                                : Optional.empty();

                        // Lấy ngày từ departTime của OUTBOUND transport
                        LocalDate departDate = outboundTransportOpt
                                .map(t -> t.getDepartTime().toLocalDate())
                                .orElse(null);

                        return TourDepartureDateResponseDTO.builder()
                                .departureID(departure.getDepartureID())
                                .departureDate(departDate)
                                .build();
                    })
                    // Bước 3: Lọc chỉ lấy những departure có ngày khởi hành trong tương lai
                    .filter(d -> d.getDepartureDate() != null && d.getDepartureDate().isAfter(today))
                    // Bước 4: Sắp xếp theo ngày tăng dần
                    .sorted((d1, d2) -> d1.getDepartureDate().compareTo(d2.getDepartureDate()))
                    .collect(Collectors.toList());

            dto.setDepartureDates(departureDates);

            // b) Lấy Giá Thấp Nhất (originalPrice của ADULT thấp nhất trong các DeparturePricing)
            // Chỉ tính từ các departure hợp lệ: status = true và ngày trong tương lai
            Long minPrice = tour.getDepartures().stream()
                    .filter(departure -> departure.getStatus() != null && departure.getStatus())
                    .filter(departure -> {
                        if (departure.getTransports() == null) {
                            return false;
                        }

                        Optional<DepartureTransport> outboundTransportOpt = departure.getTransports().stream()
                                .filter(t -> t.getType() == TransportType.OUTBOUND)
                                .min((t1, t2) -> t1.getDepartTime().compareTo(t2.getDepartTime()));

                        if (outboundTransportOpt.isEmpty()) {
                            return false;
                        }

                        // Lấy ngày từ departTime của OUTBOUND transport
                        LocalDate departDate = outboundTransportOpt.get().getDepartTime().toLocalDate();
                        return departDate.isAfter(today);
                    })
                    // Bước 3: Lấy tất cả pricings từ các departure hợp lệ
                    .flatMap(departure -> departure.getPricings() != null
                            ? departure.getPricings().stream()
                            : java.util.stream.Stream.empty())
                    // Bước 4: Chỉ lấy giá của ADULT
                    .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                    // Bước 5: Lấy originalPrice
                    .map(DeparturePricing::getOriginalPrice)
                    // Bước 6: Tìm giá thấp nhất
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

    public TourResponseDTO convertToTourFavoriteReponsetoryDTO(Tour tour, Set<Integer> favoriteTourIds) {
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
        LocalDate today = LocalDate.now();
        // 4. Lấy Danh sách Ngày Khởi Hành (departureDates) & Giá Thấp Nhất (money)
        if (tour.getDepartures() != null && !tour.getDepartures().isEmpty()) {
            List<TourDepartureDateResponseDTO> departureDates = tour.getDepartures().stream()
                    // Bước 1: Lọc departure có status = true
                    .filter(departure -> departure.getStatus() != null && departure.getStatus())
                    // Bước 2: Lấy OUTBOUND transport và ngày khởi hành
                    .map(departure -> {
                        // Tìm transport OUTBOUND (chiều đi) - lấy transport có departTime sớm nhất nếu có nhiều
                        Optional<DepartureTransport> outboundTransportOpt = departure.getTransports() != null
                                ? departure.getTransports().stream()
                                .filter(t -> t.getType() == TransportType.OUTBOUND)
                                .min((t1, t2) -> t1.getDepartTime().compareTo(t2.getDepartTime()))
                                : Optional.empty();

                        // Lấy ngày từ departTime của OUTBOUND transport
                        LocalDate departDate = outboundTransportOpt
                                .map(t -> t.getDepartTime().toLocalDate())
                                .orElse(null);

                        return TourDepartureDateResponseDTO.builder()
                                .departureID(departure.getDepartureID())
                                .departureDate(departDate)
                                .build();
                    })
                    // Bước 3: Lọc chỉ lấy những departure có ngày khởi hành trong tương lai
                    .filter(d -> d.getDepartureDate() != null && d.getDepartureDate().isAfter(today))
                    // Bước 4: Sắp xếp theo ngày tăng dần
                    .sorted((d1, d2) -> d1.getDepartureDate().compareTo(d2.getDepartureDate()))
                    .collect(Collectors.toList());

            dto.setDepartureDates(departureDates);


            Long minPrice = tour.getDepartures().stream()
                    // Bước 1: Lọc departure có status = true
                    .filter(departure -> departure.getStatus() != null && departure.getStatus())
                    // Bước 2: Kiểm tra có OUTBOUND transport và ngày khởi hành trong tương lai
                    .filter(departure -> {
                        if (departure.getTransports() == null) {
                            return false;
                        }

                        Optional<DepartureTransport> outboundTransportOpt = departure.getTransports().stream()
                                .filter(t -> t.getType() == TransportType.OUTBOUND)
                                .min((t1, t2) -> t1.getDepartTime().compareTo(t2.getDepartTime()));

                        if (outboundTransportOpt.isEmpty()) {
                            return false;
                        }

                        // Lấy ngày từ departTime của OUTBOUND transport
                        LocalDate departDate = outboundTransportOpt.get().getDepartTime().toLocalDate();
                        return departDate.isAfter(today);
                    })
                    // Bước 3: Lấy tất cả pricings từ các departure hợp lệ
                    .flatMap(departure -> departure.getPricings() != null
                            ? departure.getPricings().stream()
                            : java.util.stream.Stream.empty())
                    // Bước 4: Chỉ lấy giá của ADULT
                    .filter(p -> p.getPassengerType() == PassengerType.ADULT)
                    // Bước 5: Lấy originalPrice
                    .map(DeparturePricing::getOriginalPrice)
                    // Bước 6: Tìm giá thấp nhất
                    .min(BigDecimal::compareTo)
                    // Chuyển BigDecimal sang Long (hoặc 0L nếu không tìm thấy)
                    .map(BigDecimal::longValue)
                    .orElse(0L);
            dto.setMoney(minPrice); // <-- Gán giá trị minPrice đã tìm được
        } else {
            dto.setMoney(0L);
            dto.setDepartureDates(List.of());
        }

        if (favoriteTourIds != null && favoriteTourIds.contains(tour.getTourID())) {
            dto.setIsFavorite(true);
        } else {
            dto.setIsFavorite(false);
        }
        return dto;
    }
}