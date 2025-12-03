package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.BookingRequestDTO;
import com.tourism.backend.dto.response.BookingDetailResponseDTO;
import com.tourism.backend.dto.response.BookingFlightDTO;
import com.tourism.backend.dto.response.CouponDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.BookingStatus;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final TourRepository tourRepository;
    private final TourDepartureRepository departureRepository;
    private final CouponRepository couponRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public TourBookingInfoDTO getTourBookingInfo(String tourCode, Integer departureId) {
        Tour tour = tourRepository.findByTourCode(tourCode)
                .orElseThrow(() -> new RuntimeException("Tour not found with tour code: " + tourCode));

        String tourImage = (tour.getImages() != null && !tour.getImages().isEmpty())
                ? tour.getImages().get(0).getImageURL()
                : null;

        TourBookingInfoDTO dto = new TourBookingInfoDTO();
        dto.setTourId(tour.getTourID());
        dto.setTourName(tour.getTourName());
        dto.setTourCode(tour.getTourCode());
        dto.setImage(tourImage);

        if (departureId != null) {
            TourDeparture departure = departureRepository.findById(departureId)
                    .orElseThrow(() -> new RuntimeException("Departure not found"));

            // Số chỗ còn lại
            dto.setAvailableSlots(departure.getAvailableSlots());

            // Giá tour
            List<DeparturePricing> pricings = departure.getPricings();
            dto.setAdultPrice(findPriceByType(pricings, PassengerType.ADULT));
            dto.setChildPrice(findPriceByType(pricings, PassengerType.CHILD));
            dto.setInfantPrice(findPriceByType(pricings, PassengerType.INFANT));
            dto.setSingleRoomSurcharge(findPriceByType(pricings, PassengerType.SINGLE_SUPPLEMENT));

            // Chuyến bay
            List<DepartureTransport> transports = departure.getTransports();
            if (transports != null && !transports.isEmpty()) {
                transports.sort(Comparator.comparing(DepartureTransport::getDepartTime));
                dto.setOutboundFlight(mapToFlightDTO(transports.get(0)));
                if (transports.size() > 1) {
                    dto.setInboundFlight(mapToFlightDTO(transports.get(transports.size() - 1)));
                }
            }

            LocalDateTime now = LocalDateTime.now();

            // 1. Lấy Coupon dành riêng cho Departure (Ưu tiên cao nhất)
            List<Coupon> depCoupons = couponRepository.findByDepartureId(departureId, now);
            if (!depCoupons.isEmpty()) {
                dto.setDepartureCoupon(mapToCouponDTO(depCoupons.get(0)));
            }

            // 2. Lấy danh sách Coupon Global (Cho khách chọn thêm)
            List<Coupon> globalCoupons = couponRepository.findGlobalCoupons(now);
            dto.setGlobalCoupons(
                    globalCoupons.stream()
                            .map(this::mapToCouponDTO)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    @Override
    @Transactional
    public BookingDetailResponseDTO createBooking(BookingRequestDTO request) {
        TourDeparture departure = departureRepository.findById(request.getDepartureId())
                .orElseThrow(() -> new RuntimeException("Departure not found!"));

        // Count seat
        long seatCount = request.getPassengers().stream()
                .filter(p -> !"INFANT".equalsIgnoreCase(p.getType()))
                .count();

        int updatedRows = departureRepository.decreaseAvailableSlots(request.getDepartureId(), (int) seatCount);
        if (updatedRows == 0) {
            throw new RuntimeException("Regret, there are not enough seats or they are already booked");
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal subSurcharge = BigDecimal.ZERO;
        List<DeparturePricing> pricings = departure.getPricings();

        Booking booking = new Booking();
        List<BookingPassenger> passengerEntities = new ArrayList<>();

        for (BookingRequestDTO.PassengerRequest pReq : request.getPassengers()) {
            BigDecimal ticketPrice = findPriceByType(pricings, pReq.getType());

            BigDecimal surcharge = BigDecimal.ZERO;
            if (pReq.isSingleRoom()) {
                surcharge = findPriceByType(pricings, "SINGLE_SUPPLEMENT");
            }

            subSurcharge = subSurcharge.add(surcharge);
            subTotal = subTotal.add(ticketPrice);

            BookingPassenger pEntity = new BookingPassenger();
            pEntity.setFullName(pReq.getFullName());
            pEntity.setGender(pReq.getGender());
            pEntity.setDateOfBirth(pReq.getDateOfBirth());
            pEntity.setPassengerType(pReq.getType());
            pEntity.setBasePrice(ticketPrice);
            pEntity.setRequiresSingleRoom(pReq.isSingleRoom());
            pEntity.setSingleRoomSurcharge(surcharge);
            pEntity.setBooking(booking);
            passengerEntities.add(pEntity);
        }

        // XỬ LÝ NHIỀU COUPON
        BigDecimal couponDiscount = BigDecimal.ZERO;
        List<Coupon> appliedCoupons = new ArrayList<>();

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            for (String code : request.getCouponCode()) {
                Coupon coupon = couponRepository.findByCouponCode(code)
                        .orElseThrow(() -> new RuntimeException("Coupon code not found: " + code));

                // Kiểm tra minOrderValue
                if (coupon.getMinOrderValue() != null && subTotal.compareTo(coupon.getMinOrderValue()) < 0) {
                    throw new RuntimeException("Order total must be at least " + coupon.getMinOrderValue() + " to use coupon " + code);
                }

                couponDiscount = couponDiscount.add(BigDecimal.valueOf(coupon.getDiscountAmount()));

                // Tăng lượt dùng
                coupon.setUsageCount(coupon.getUsageCount() + 1);
                couponRepository.save(coupon);

                appliedCoupons.add(coupon);
            }
        }

        BigDecimal pointDiscount = BigDecimal.ZERO;

        if (request.getPointsUsed() != null && request.getPointsUsed() > 0) {
            User user = userRepository.findByEmail(request.getContactEmail())
                    .orElseThrow(() -> new RuntimeException("This email is not a registered member, reward points cannot be used!"));

            BigDecimal pointsToRedeem = BigDecimal.valueOf(request.getPointsUsed());

            if (user.getCoinBalance().compareTo(pointsToRedeem) < 0) {
                throw new RuntimeException("Insufficient points balance! You just have " + user.getCoinBalance() + " point.");
            }

            pointDiscount = pointsToRedeem.multiply(BigDecimal.valueOf(1000));

            user.setCoinBalance(user.getCoinBalance().subtract(pointsToRedeem));
            userRepository.save(user);
            booking.setUser(user);
        }

        BigDecimal finalTotal = subTotal.add(subSurcharge).subtract(couponDiscount).subtract(pointDiscount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        booking.setBookingDate(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);

        booking.setContactFullName(request.getContactFullName());
        booking.setContactEmail(request.getContactEmail());
        booking.setContactPhone(request.getContactPhone());
        booking.setContactAddress(request.getContactAddress());
        booking.setCustomerNote(request.getCustomerNote());

        booking.setTotalPassengers(request.getPassengers().size());
        booking.setSubtotalPrice(subTotal);
        booking.setSurcharge(subSurcharge);
        booking.setCouponDiscount(couponDiscount);
        booking.setPaidByCoin(pointDiscount);
        booking.setTotalPrice(finalTotal);

        if (!request.getCouponCode().isEmpty()) {
            booking.setAppliedCouponCodes(
                    String.join(",", request.getCouponCode())
            );
        }

        booking.setTourDeparture(departure);
        booking.setPassengers(passengerEntities);

        bookingRepository.save(booking);

        return mapToBookingDetailDTO(booking);
    }

    @Override
    public BookingDetailResponseDTO getBookingDetail(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new RuntimeException("Booking not found with code: " + bookingCode));
        return mapToBookingDetailDTO(booking);
    }

    private BookingDetailResponseDTO mapToBookingDetailDTO(Booking booking) {
        Tour tour = booking.getTourDeparture().getTour();
        TourDeparture departure = booking.getTourDeparture();

        // Map hành khách
        List<BookingDetailResponseDTO.PassengerDTO> passengerDTOs = booking.getPassengers().stream()
                .map(p -> BookingDetailResponseDTO.PassengerDTO.builder()
                        .fullName(p.getFullName())
                        .gender(p.getGender())
                        .dateOfBirth(p.getDateOfBirth().toString())
                        .type(p.getPassengerType())
                        .singleRoom(p.getRequiresSingleRoom())
                        .build())
                .collect(Collectors.toList());

        // SỬA: Map transports
        List<DepartureTransport> transports = departure.getTransports();
        BookingFlightDTO outbound = null;
        BookingFlightDTO inbound = null;

        if (transports != null && !transports.isEmpty()) {
            transports.sort(Comparator.comparing(DepartureTransport::getDepartTime));
            outbound = mapToFlightDTO(transports.get(0));

            if (transports.size() > 1) {
                inbound = mapToFlightDTO(transports.get(transports.size() - 1));
            }
        }

        // Lấy danh sách coupon codes
        List<String> appliedCouponCodes = new ArrayList<>();
        if (booking.getAppliedCouponCodes() != null && !booking.getAppliedCouponCodes().isEmpty()) {
            appliedCouponCodes = Arrays.asList(booking.getAppliedCouponCodes().split(","));
        }

        // Tính tiền đã trả (Giả sử mới tạo là 0)
        BigDecimal paid = BigDecimal.ZERO;

        return BookingDetailResponseDTO.builder()
                .bookingCode(booking.getBookingCode())
                .createdDate(booking.getCreatedAt())
                .status(booking.getBookingStatus().name())
                .paymentDeadline(booking.getCreatedAt().plusHours(24))

                // Financial
                .originalPrice(booking.getTotalPrice())
                .paidAmount(paid)
                .remainingAmount(booking.getTotalPrice().subtract(paid))

                // Lists
                .passengers(passengerDTOs)
                .appliedCouponCodes(appliedCouponCodes)
                .outboundTransport(outbound)
                .inboundTransport(inbound)

                // Tour Info
                .tourName(tour.getTourName())
                .tourCode(tour.getTourCode())
                .tourImage(tour.getImages().isEmpty() ? null : tour.getImages().get(0).getImageURL())
                .duration(tour.getDuration())
                .build();
    }

    private BigDecimal findPriceByType(List<DeparturePricing> pricings, String typeName) {
        return pricings.stream()
                .filter(p -> p.getPassengerType().name().equals(typeName))
                .findFirst()
                .map(DeparturePricing::getSalePrice)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal findPriceByType(List<DeparturePricing> pricings, PassengerType type) {
        if (pricings == null) return BigDecimal.ZERO;
        return pricings.stream()
                .filter(p -> p.getPassengerType() == type)
                .findFirst()
                .map(DeparturePricing::getSalePrice)
                .orElse(BigDecimal.ZERO);
    }

    private CouponDTO mapToCouponDTO(Coupon coupon) {
        return new CouponDTO(
                coupon.getCouponID(),
                coupon.getCouponCode(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getMinOrderValue()
        );
    }

    private BookingFlightDTO mapToFlightDTO(DepartureTransport transport) {
        BookingFlightDTO flight = new BookingFlightDTO();
        flight.setTransportCode(transport.getTransportCode());
        flight.setAirlineName(transport.getVehicleName());
        flight.setStartPoint(transport.getStartPoint());
        flight.setEndPoint(transport.getEndPoint());
        flight.setStartPointName(getLocationName(transport.getStartPoint()));
        flight.setEndPointName(getLocationName(transport.getEndPoint()));
        flight.setDepartTime(transport.getDepartTime());
        flight.setArrivalTime(transport.getArrivalTime());
        return flight;
    }

    private String getLocationName(String locationCode) {
        if (locationCode == null) return "";
        return locationRepository.findByAirportCode(locationCode)
                .map(Location::getAirportName)
                .orElse(locationCode);
    }
}