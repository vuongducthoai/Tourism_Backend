package com.tourism.backend.service.impl;

import com.tourism.backend.convert.LocationConverter;
import com.tourism.backend.convert.TourConvert;
import com.tourism.backend.convert.TourSpecialConvert;
import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.requestDTO.SearchToursRequestDTO;
import com.tourism.backend.dto.response.TourDetailResponseDTO;
import com.tourism.backend.dto.response.*;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.dto.responseDTO.TourSpecialResponseDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.enums.TransportType;
import com.tourism.backend.repository.CouponRepository;
import com.tourism.backend.entity.Location;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourImage;
import com.tourism.backend.repository.LocationRepository;
import com.tourism.backend.repository.TourDepartureRepository;
import com.tourism.backend.repository.TourRepository;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.FavoriteTourService;
import com.tourism.backend.service.LocationService;
import com.tourism.backend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourConvert tourConvert;
    private final TourRepository tourRepository;
    private final CloudinaryService cloudinaryService;
    private final LocationRepository locationRepository;
    private final CouponRepository couponRepository;
    private final LocationService locationService;
    private final LocationConverter locationConverter;
    private final TourSpecialConvert tourSpecialConvert;
    private final TourDepartureRepository tourDepartureRepository;
    private final FavoriteTourService favoriteTourService;
    @Override // Ghi đè phương thức từ Interface
    @Transactional
    public Tour createTourWithImages(TourCreateDTO dto) throws IOException {
        // 1. Map dữ liệu từ DTO sang Entity Tour
        Tour tour = new Tour();

        // Nếu DTO không gửi tourCode, để null để Entity tự sinh (@PrePersist)
        tour.setTourCode(dto.getTourCode());

        tour.setTourName(dto.getTourName());
        tour.setDuration(dto.getDuration());
//        tour.setTransportation(dto.getTransportation());

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
        List<Tour> tours = tourRepository.findAllToursForListDisplay();
        return tours.stream()
                .map(tourConvert::convertToTourReponsetoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailResponseDTO getTourDetail(String tourCode) {
        // ============================================
        // TOUR
        // ============================================
        Tour tour = tourRepository.findByTourCode(tourCode)
                .orElseThrow(() -> new RuntimeException("Tour not found: " + tourCode));

        List<TourDeparture> departures = tour.getDepartures();
        if (departures == null || departures.isEmpty()) {
            throw new RuntimeException("No departures found for tour: " + tourCode);
        }


        // ============================================
        // DEPARTURE
        // ============================================
        List<TourDetailResponseDTO.DepartureDTO> departureDTOs = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (TourDeparture dep : departures) {
            System.out.println("=== Processing Departure ID: " + dep.getDepartureID() + " ===");

            // Validate transports
            if (dep.getTransports() == null || dep.getTransports().isEmpty() || !dep.getStatus()) {
                System.out.println("⚠ Skipped: No transports");
                continue;
            }

            // Tìm OUTBOUND transport
            DepartureTransport outbound = dep.getTransports().stream()
                    .filter(t -> TransportType.OUTBOUND.equals(t.getType()))
                    .findFirst()
                    .orElse(null);

            if (outbound == null || outbound.getDepartTime() == null) {
                System.out.println("⚠ Skipped: No valid OUTBOUND transport");
                continue;
            }

            LocalDate realDepartureDate = outbound.getDepartTime().toLocalDate();

            // Validate departure date
            if (realDepartureDate.isBefore(today)) {
                System.out.println("⚠ Skipped: Date in past (" + realDepartureDate + ")");
                continue;
            }

            System.out.println("✓ Valid departure date: " + realDepartureDate);

            // ============================================
            // 2.1. MAP TRANSPORTS
            // ============================================
            List<TourDetailResponseDTO.TransportDTO> transportDTOs = dep.getTransports().stream()
                    .map(t -> {
                        String startFullName = locationRepository.findByAirportCode(t.getStartPoint())
                                .map(Location::getAirportName)
                                .orElse(t.getStartPoint());

                        String endFullName = locationRepository.findByAirportCode(t.getEndPoint())
                                .map(Location::getAirportName)
                                .orElse(t.getEndPoint());

                        return TourDetailResponseDTO.TransportDTO.builder()
                                .type(t.getType().name())
                                .transportCode(t.getTransportCode())
                                .vehicleName(t.getVehicleName())
                                .startPoint(t.getStartPoint())
                                .startPointName(startFullName)
                                .endPoint(t.getEndPoint())
                                .endPointName(endFullName)
                                .departTime(t.getDepartTime())
                                .arrivalTime(t.getArrivalTime())
                                .build();
                    })
                    .collect(Collectors.toList());

            // ============================================
            // 2.2. VALIDATE PRICINGS
            // ============================================
            if (dep.getPricings() == null || dep.getPricings().isEmpty()) {
                System.out.println("⚠ Skipped: No pricings");
                continue;
            }

            // Lấy giá ADULT để tính coupon
            BigDecimal adultSalePrice = dep.getPricings().stream()
                    .filter(p -> PassengerType.ADULT.equals(p.getPassengerType()))
                    .findFirst()
                    .map(DeparturePricing::getSalePrice)
                    .orElse(BigDecimal.ZERO);

            if (adultSalePrice.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("⚠ Skipped: Invalid adult price");
                continue;
            }

            // ============================================
            // 2.3. TÌM COUPON DEPARTURE-SPECIFIC
            // ============================================
            String departureCouponCode = null;
            BigDecimal departureCouponDiscount = BigDecimal.ZERO;

            Optional<Coupon> departureCouponOpt = couponRepository.findBestDepartureCoupon(
                    dep.getDepartureID(),
                    adultSalePrice,
                    now
            );

            if (departureCouponOpt.isPresent()) {
                Coupon departureCoupon = departureCouponOpt.get();
                departureCouponCode = departureCoupon.getCouponCode();
                departureCouponDiscount = BigDecimal.valueOf(departureCoupon.getDiscountAmount());

                System.out.println("✓ Departure Coupon: " + departureCouponCode + " (-" + departureCouponDiscount + ")");
            } else {
                System.out.println("ℹ No departure-specific coupon");
            }

            // ============================================
            // 2.4. TÌM COUPON GLOBAL
            // ============================================
            String globalCouponCode = null;
            BigDecimal globalCouponDiscount = BigDecimal.ZERO;

            // Tính giá sau khi áp departure coupon để check minOrderValue của global coupon
            BigDecimal priceAfterDepartureCoupon = adultSalePrice.subtract(departureCouponDiscount);
            if (priceAfterDepartureCoupon.compareTo(BigDecimal.ZERO) < 0) {
                priceAfterDepartureCoupon = BigDecimal.ZERO;
            }

            Optional<Coupon> globalCouponOpt = couponRepository.findBestGlobalCoupon(
                    priceAfterDepartureCoupon, // Kiểm tra minOrderValue dựa trên giá đã giảm
                    now
            );

            if (globalCouponOpt.isPresent()) {
                Coupon globalCoupon = globalCouponOpt.get();
                globalCouponCode = globalCoupon.getCouponCode();
                globalCouponDiscount = BigDecimal.valueOf(globalCoupon.getDiscountAmount());

                System.out.println("✓ Global Coupon: " + globalCouponCode + " (-" + globalCouponDiscount + ")");
            } else {
                System.out.println("ℹ No global coupon available");
            }

            // Tổng discount từ cả 2 coupon
            BigDecimal totalDiscountAmount = departureCouponDiscount.add(globalCouponDiscount);

            System.out.println("💰 Total Discount: -" + totalDiscountAmount);

            // Tạo biến final để dùng trong lambda
            final BigDecimal finalTotalDiscount = totalDiscountAmount;

            // ============================================
            // 2.5. MAP PRICINGS (Với finalPrice đã tính cả 2 coupon)
            // ============================================
            List<TourDetailResponseDTO.PricingDTO> pricingDTOs = dep.getPricings().stream()
                    .map(p -> {
                        BigDecimal finalPrice = p.getSalePrice();

                        // Chỉ áp dụng discount cho ADULT
                        if (PassengerType.ADULT.equals(p.getPassengerType())) {
                            finalPrice = p.getSalePrice().subtract(finalTotalDiscount);
                            if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                                finalPrice = BigDecimal.ZERO;
                            }
                        }

                        return TourDetailResponseDTO.PricingDTO.builder()
                                .passengerType(p.getPassengerType().name())
                                .description(p.getAgeDescription())
                                .originalPrice(p.getOriginalPrice())
                                .salePrice(p.getSalePrice())
                                .finalPrice(finalPrice)
                                .build();
                    })
                    .collect(Collectors.toList());

            // ============================================
            // 2.6. TẠO DEPARTURE DTO
            // ============================================
            TourDetailResponseDTO.DepartureDTO departureDTO = TourDetailResponseDTO.DepartureDTO.builder()
                    .departureId(dep.getDepartureID())
                    .departureDate(realDepartureDate)
                    .availableSlots(dep.getAvailableSlots())
                    .transports(transportDTOs)
                    .pricings(pricingDTOs)
                    // Coupon info
                    .departureCouponCode(departureCouponCode)
                    .departureCouponDiscount(departureCouponDiscount)
                    .globalCouponCode(globalCouponCode)
                    .globalCouponDiscount(globalCouponDiscount)
                    .totalDiscountAmount(totalDiscountAmount)
                    .build();

            departureDTOs.add(departureDTO);
            System.out.println("✓ Departure processed successfully\n");
        }

        // Sắp xếp theo ngày tăng dần
        departureDTOs.sort(Comparator.comparing(TourDetailResponseDTO.DepartureDTO::getDepartureDate));

        System.out.println("=== Total valid departures: " + departureDTOs.size() + " ===");

        // ============================================
        // 3. TÍNH HEADER PRICING (Từ chuyến gần nhất)
        // ============================================
        BigDecimal headerOriginalPrice = BigDecimal.ZERO;
        BigDecimal headerSalePrice = BigDecimal.ZERO;
        BigDecimal headerCouponDiscount = BigDecimal.ZERO;
        BigDecimal headerFinalPrice = BigDecimal.ZERO;
        String headerBestCoupon = null;
        int totalDiscountPercent = 0;

        if (!departureDTOs.isEmpty()) {
            TourDetailResponseDTO.DepartureDTO nearestDeparture = departureDTOs.get(0);

            TourDetailResponseDTO.PricingDTO adultPricing = nearestDeparture.getPricings().stream()
                    .filter(p -> "ADULT".equals(p.getPassengerType()))
                    .findFirst()
                    .orElse(null);

            if (adultPricing != null) {
                headerOriginalPrice = adultPricing.getOriginalPrice();
                headerSalePrice = adultPricing.getSalePrice();
                headerFinalPrice = adultPricing.getFinalPrice();
                headerCouponDiscount = nearestDeparture.getTotalDiscountAmount();

                // Hiển thị cả 2 mã coupon nếu có
                List<String> appliedCoupons = new ArrayList<>();
                if (nearestDeparture.getDepartureCouponCode() != null) {
                    appliedCoupons.add(nearestDeparture.getDepartureCouponCode());
                }
                if (nearestDeparture.getGlobalCouponCode() != null) {
                    appliedCoupons.add(nearestDeparture.getGlobalCouponCode());
                }
                headerBestCoupon = String.join(" + ", appliedCoupons);

                // Tính % giảm giá tổng
                if (headerOriginalPrice.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal totalDiscount = headerOriginalPrice.subtract(headerFinalPrice);
                    totalDiscountPercent = totalDiscount
                            .divide(headerOriginalPrice, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .intValue();
                }

                System.out.println("=== Header Pricing ===");
                System.out.println("Original: " + headerOriginalPrice);
                System.out.println("Sale: " + headerSalePrice);
                System.out.println("Coupons: " + headerBestCoupon);
                System.out.println("Total Discount: -" + headerCouponDiscount);
                System.out.println("Final: " + headerFinalPrice);
                System.out.println("Discount: " + totalDiscountPercent + "%");
            }
        }

        // ============================================
        // 4. MAP IMAGES
        // ============================================
        List<String> images = new ArrayList<>();
        if (tour.getImages() != null) {
            images = tour.getImages().stream()
                    .map(TourImage::getImageURL)
                    .collect(Collectors.toList());
        }

        // ============================================
        // 4. MAP Video
        // ============================================
        String videoUrl = tour.getMediaList().stream()
                .filter(TourMedia::getIsPrimary)
                .map(TourMedia::getMediaUrl)
                .findFirst()
                .orElse(null);


        // ============================================
        // 5. MAP ITINERARY
        // ============================================
        List<TourDetailResponseDTO.ItineraryDTO> itinerary = new ArrayList<>();
        if (tour.getItineraryDays() != null) {
            itinerary = tour.getItineraryDays().stream()
                    .sorted(Comparator.comparing(ItineraryDay::getDayNumber))
                    .map(d -> TourDetailResponseDTO.ItineraryDTO.builder()
                            .dayNumber(d.getDayNumber())
                            .title(d.getTitle())
                            .meals(d.getMeals())
                            .details(d.getDetails())
                            .build())
                    .collect(Collectors.toList());
        }

        // ============================================
        // 6. MAP POLICY & BRANCH CONTACT
        // ============================================
        TourDetailResponseDTO.PolicyDTO policyDTO = null;
        TourDetailResponseDTO.BranchContactDTO branchContactDTO = null;

        if (!departures.isEmpty()) {
            PolicyTemplate template = departures.get(0).getPolicyTemplate();

            if (template != null) {
                policyDTO = TourDetailResponseDTO.PolicyDTO.builder()
                        .templateName(template.getTemplateName())
                        .tourPriceIncludes(template.getTourPriceIncludes())
                        .tourPriceExcludes(template.getTourPriceExcludes())
                        .childPricingNotes(template.getChildPricingNotes())
                        .registrationConditions(template.getRegistrationConditions())
                        .regularDayCancellationRules(template.getRegularDayCancellationRules())
                        .holidayCancellationRules(template.getHolidayCancellationRules())
                        .forceMajeureRules(template.getForceMajeureRules())
                        .packingList(template.getPackingList())
                        .cancellationRules(template.getRegularDayCancellationRules())
                        .paymentConditions(template.getPaymentConditions())
                        .build();

                BranchContact branchContact = template.getContact();
                if (branchContact != null) {
                    branchContactDTO = TourDetailResponseDTO.BranchContactDTO.builder()
                            .branchName(branchContact.getBranchName())
                            .email(branchContact.getEmail())
                            .phone(branchContact.getPhone())
                            .address(branchContact.getAddress())
                            .isHeadOffice(branchContact.getIsHeadOffice())
                            .build();
                }
            }
        }

        // ============================================
        // 7. BUILD FINAL RESPONSE
        // ============================================
        return TourDetailResponseDTO.builder()
                // Basic Info
                .tourId(tour.getTourID())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .duration(tour.getDuration())
                .transportation(tour.getTransportation())
                .attractions(tour.getAttractions())
                .meals(tour.getMeals())
                .suitableCustomer(tour.getSuitableCustomer())
                .idealTime(tour.getIdealTime())
                .tripTransportation(tour.getTripTransportation())
                .startLocation(tour.getStartLocation().getName())
                .endLocation(tour.getEndLocation().getName())
                // Header Pricing
                .originalPrice(headerOriginalPrice)
                .salePrice(headerSalePrice)
                .couponDiscount(headerCouponDiscount)
                .bestCouponCode(headerBestCoupon)
                .finalPrice(headerFinalPrice)
                .totalDiscountPercentage(totalDiscountPercent)
                // Lists
                .images(images)
                .videoUrl(videoUrl)
                .itinerary(itinerary)
                .departures(departureDTOs)
                .policy(policyDTO)
                .branchContact(branchContactDTO)
                .build();
    }

    @Override
    public List<TourCardResponseDTO> getRelatedTours(String currentTourCode) {
        Tour currentTour = tourRepository.findByTourCode(currentTourCode)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        List<Tour> relatedTours = tourRepository.findRelatedTours(
                currentTour.getEndLocation().getLocationID(),
                currentTour.getTourID(),
                PageRequest.of(0, 3)
        );
        List<TourCardResponseDTO> response = new ArrayList<>();
        LocalDateTime today = LocalDate.now().atStartOfDay();

        for(Tour t : relatedTours){
            String thumbnail = t.getImages().stream()
                    .filter(TourImage::getIsMainImage)
                    .findFirst()
                    .map(TourImage::getImageURL)
                    .orElse(t.getImages().isEmpty() ? null : t.getImages().get(0).getImageURL());

            BigDecimal minPrice = BigDecimal.ZERO;
            BigDecimal originalPrice = BigDecimal.ZERO;

            TourDeparture nextDep = t.getDepartures().stream()
                    .filter(d -> d.getDepartureDate().isAfter(today))
                    .min(Comparator.comparing(TourDeparture::getDepartureDate))
                    .orElse(null);

            if (nextDep != null) {
                DeparturePricing adultPrice = nextDep.getPricings().stream()
                        .filter(p -> "ADULT".equals(p.getPassengerType().name()))
                        .findFirst().orElse(null);

                if (adultPrice != null) {
                    minPrice = adultPrice.getSalePrice();
                    originalPrice = adultPrice.getOriginalPrice();
                }
            }
            response.add(TourCardResponseDTO.builder()
                    .tourId(t.getTourID())
                    .tourName(t.getTourName())
                    .tourCode(t.getTourCode())
                    .duration(t.getDuration())
                    .startLocation(t.getStartLocation().getName())
                    .image(thumbnail)
                    .price(minPrice)
                    .originalPrice(originalPrice)
                    .build());
        }
        return response;

    }

    @Override
    public Page<TourSimpleResponse> getAllToursSimple(Pageable pageable) {
        Page<Tour> tours = tourRepository.findAll(pageable);

        // Map từ Entity sang DTO gọn nhẹ
        return tours.map(tour -> TourSimpleResponse.builder()
                .tourID(tour.getTourID())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartureSimpleResponse> getDeparturesByTour(Integer tourId) {
        LocalDateTime tomorrow = LocalDate.now().atStartOfDay();

        List<TourDeparture> departures = tourDepartureRepository
                .findByTour_TourIDAndDepartureDateAfterAndStatusTrueOrderByDepartureDateAsc(tourId, tomorrow);

        // Map sang DTO
        return departures.stream()
                .map(dep -> DepartureSimpleResponse.builder()
                        .departureID(dep.getDepartureID())
                        .departureDate(dep.getDepartureDate())
                        .availableSlots(dep.getAvailableSlots())
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<TourSpecialResponseDTO> getTop10DeepestDiscountTours() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now(); // ✅ Thêm biến now

        List<Tour> tours = tourRepository.findAllToursWithPricingAndTransport();

        return tours.stream()
                // 1. Lọc Tour và Departure hợp lệ
                .filter(tour -> tour.getStatus() != null && tour.getStatus())
                .flatMap(tour -> tour.getDepartures() != null ?
                        tour.getDepartures().stream() :
                        java.util.stream.Stream.empty())
                .filter(departure -> departure.getStatus() != null && departure.getStatus())
                .filter(departure -> {
                    if (departure.getTransports() == null) return false;

                    Optional<DepartureTransport> outboundTransportOpt = departure.getTransports().stream()
                            .filter(t -> t.getType() == TransportType.OUTBOUND)
                            .min(Comparator.comparing(DepartureTransport::getDepartTime));

                    if (outboundTransportOpt.isEmpty() || outboundTransportOpt.get().getDepartTime() == null) {
                        return false;
                    }
                    return outboundTransportOpt.get().getDepartTime().toLocalDate().isAfter(today);
                })
                // 2. Tính toán Discount cho từng Departure (✅ Truyền thêm tham số now)
                .map(departure -> new TourSpecialConvert.DiscountInfo(departure, now))

                // 3. Lọc chỉ lấy tour có discountAmount > 0
                .filter(info -> info.discountAmount != null && info.discountAmount > 0)

                // 4. Loại bỏ trùng lặp tourCode, giữ lại departure có discountAmount lớn nhất
                .collect(Collectors.toMap(
                        info -> info.departure.getTour().getTourCode(),
                        info -> info,
                        (existing, replacement) ->
                                existing.discountAmount >= replacement.discountAmount ?
                                        existing : replacement
                ))
                .values().stream()

                // 5. SẮP XẾP GIẢM DẦN THEO discountAmount
                .sorted(Comparator.comparing(
                        (TourSpecialConvert.DiscountInfo info) -> info.discountAmount,
                        Comparator.reverseOrder()
                ))

                // 6. Lấy Top 10
                .limit(10)

                // 7. Map sang DTO
                .map(tourSpecialConvert::mapToTourSpecialResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<TourResponseDTO> searchTours(SearchToursRequestDTO dto, Integer userId) {
        List<Tour> tours = tourRepository.searchToursDynamically(dto);

        Set<Integer> favoriteTourIds = (userId != null)
                ? favoriteTourService.getFavoriteTourIdsByUserId(userId)
                : Collections.emptySet();

        return tours.stream()
                .map(tour -> tourConvert.convertToTourFavoriteReponsetoryDTO(tour, favoriteTourIds))
                // Lọc bỏ tour không có ngày khởi hành
                .filter(tourDto ->
                        tourDto.getDepartureDates() != null
                                && !tourDto.getDepartureDates().isEmpty()
                )
                .collect(Collectors.toList());
    }

}