package com.tourism.backend.service.impl;

import com.tourism.backend.convert.TourConvert;
import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.response.DepartureDTO;
import com.tourism.backend.dto.response.DeparturePricingDTO;
import com.tourism.backend.dto.response.TourDetailResponseDTO;
import com.tourism.backend.dto.response.TransportDTO;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.repository.CouponRepository;
import com.tourism.backend.repository.LocationRepository;
import com.tourism.backend.repository.TourRepository;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourConvert tourConvert; // 👈 Inject TourConvert
    private final TourRepository tourRepository;
    private final CloudinaryService cloudinaryService;
    private final LocationRepository locationRepository;
    private CouponRepository couponRepository;

    @Override // Ghi đè phương thức từ Interface
    @Transactional
    public Tour createTourWithImages(TourCreateDTO dto) throws IOException {
        // 1. Map dữ liệu từ DTO sang Entity Tour
        Tour tour = new Tour();

        // Nếu DTO không gửi tourCode, để null để Entity tự sinh (@PrePersist)
        tour.setTourCode(dto.getTourCode());

        tour.setTourName(dto.getTourName());
        tour.setDuration(dto.getDuration());
        tour.setTransportation(dto.getTransportation());

        Location startLoc = locationRepository.findById(dto.getStartLocationId())
                .orElseThrow(() -> new RuntimeException("Start location Id not found"));
        tour.setStartLocation(startLoc);

        tour.setStartLocation(startLoc);

        Location endLoc = locationRepository.findById(dto.getEndLocationId())
                .orElseThrow(() -> new RuntimeException("End location Id not found: " + dto.getEndLocationId()));
        tour.setEndLocation(endLoc);

        tour.setAttractions(dto.getAttractions());

        // Xử lý các trường có thể null (Optional)
        // Nếu không gửi meals, set giá trị mặc định để tránh lỗi database
        tour.setMeals(dto.getMeals() != null ? dto.getMeals() : "Theo chương trình");
        tour.setHotel(dto.getHotel() != null ? dto.getHotel() : "Tiêu chuẩn");
        tour.setIdealTime(dto.getIdealTime());
        tour.setTripTransportation(dto.getTripTransportation());
        tour.setSuitableCustomer(dto.getSuitableCustomer());

        // 2. Xử lý Upload ảnh Cloudinary
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<TourImage> tourImages = new ArrayList<>();

            // Duyệt qua từng file ảnh client gửi lên
            for (int i = 0; i < dto.getImages().size(); i++) {
                MultipartFile file = dto.getImages().get(i);

                // Bỏ qua file rỗng (đề phòng lỗi gửi form)
                if (file.isEmpty()) continue;

                String subFolder = tour.getTourCode();
                if(subFolder == null) subFolder = "unknown_tour";

                // A. Gọi Cloudinary Service để upload và lấy URL về
                String imageUrl = cloudinaryService.uploadImage(file, subFolder);

                // B. Tạo Entity TourImage
                TourImage img = new TourImage();
                img.setImageURL(imageUrl);
                img.setTour(tour); // Quan hệ 2 chiều: Ảnh thuộc về Tour này

                // C. Logic chọn ảnh chính (Thumbnail)
                // Ảnh đầu tiên (index 0) sẽ là ảnh đại diện
                img.setIsMainImage(i == 0);

                // D. Thêm vào list
                tourImages.add(img);
            }

            // Gán list ảnh đã tạo vào object Tour
            tour.setImages(tourImages);
        }

        // 3. Lưu xuống Database
        // Nhờ CascadeType.ALL ở Entity Tour, nó sẽ tự động lưu luôn cả list images
        return tourRepository.save(tour);
    }

    @Override
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    @Override
    public Tour getTourByCode(String tourCode) {
        return tourRepository.findByTourCode(tourCode)
                .orElseThrow(() -> new RuntimeException("Tour with: " + tourCode + " is not found"));
    }

    @Override
    public List<TourResponseDTO> getAllToursForListDisplay() {
        // Lấy danh sách Tour Entity
        List<Tour> tours = tourRepository.findAllToursForListDisplay();
        // Chuyển đổi từng Entity Tour sang DTO TourReponsetory
        return tours.stream()
                .map(tourConvert::convertToTourReponsetoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailResponseDTO getTourDetail(String tourCode) {
        Tour tour = tourRepository.findByTourCode(tourCode)
                .orElseThrow(() -> new RuntimeException("Tour not found: " + tourCode));

        //Handle list departure and pricing
        List<DepartureDTO> departureDTOs = new ArrayList<>();
        DeparturePricingDTO pricingDTO = new DeparturePricingDTO();
        LocalDate today = LocalDate.now();

        //"Best trip" (Closest) put on Header
        DepartureDTO nearestDeparture = null;

        for(TourDeparture dep : tour.getDepartures()){
            if(dep.getDepartureDate().isBefore(today)) continue;

            List<TransportDTO> transportDTOS = dep.getTransports().stream()
                    .map(t -> TransportDTO.builder()
                            .type(t.getType().name())
                            .transportCode(t.getTransportCode())
                            .startPoint(t.getStartPoint())
                            .endPoint(t.getEndPoint())
                            .departTime(t.getDepartTime())
                            .arrivalTime(t.getArrivalTime())
                            .build())
                    .collect(Collectors.toList());

            //Map pricing
            List<DeparturePricingDTO> pricings = dep.getPricings().stream()
                    .map(p -> DeparturePricingDTO.builder()
                            .passengerType(p.getPassengerType().name())
                            .ageDescription(p.getAgeDescription())
                            .originalPrice(p.getOriginalPrice())
                            .salePrice(p.getSalePrice())
                            .build())
                    .collect(Collectors.toList());

            //FIND THE BEST COUPON FOR THIS TRIP
            String bestCode = null;
            BigDecimal discount = BigDecimal.ZERO;

            BigDecimal adultPrice = pricings.stream()
                    .filter(p -> "ADULT".equals(p.getPassengerType()))
                    .findFirst().map(DeparturePricingDTO::getSalePrice)
                    .orElse(BigDecimal.ZERO);

        }
        return null;
    }
}