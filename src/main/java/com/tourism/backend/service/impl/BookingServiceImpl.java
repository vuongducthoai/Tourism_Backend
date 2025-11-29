package com.tourism.backend.service.impl;

import com.tourism.backend.dto.response.BookingFlightDTO;
import com.tourism.backend.dto.response.CouponDTO;
import com.tourism.backend.dto.response.TourBookingInfoDTO;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.PassengerType;
import com.tourism.backend.repository.CouponRepository;
import com.tourism.backend.repository.LocationRepository;
import com.tourism.backend.repository.TourDepartureRepository;
import com.tourism.backend.repository.TourRepository;
import com.tourism.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

            //So cho con lai
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