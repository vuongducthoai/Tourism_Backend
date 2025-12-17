package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.CreateDepartureRequest;
import com.tourism.backend.dto.request.DeparturePricingRequest;
import com.tourism.backend.dto.request.DepartureTransportRequest;
import com.tourism.backend.dto.request.UpdateDepartureRequest;
import com.tourism.backend.dto.response.DepartureDetailResponse;
import com.tourism.backend.dto.response.DeparturePricingResponse;
import com.tourism.backend.dto.response.DepartureSummaryResponse;
import com.tourism.backend.dto.response.DepartureTransportResponse;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.TransportType;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.TourDepartureService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TourDepartureServiceImpl implements TourDepartureService {
    private final TourDepartureRepository departureRepository;
    private final TourRepository tourRepository;
    private final PolicyTemplateRepository policyTemplateRepository;
    private final CouponRepository couponRepository;
    private final DeparturePricingRepository pricingRepository;
    private final DepartureTransportRepository transportRepository;
    private final EntityManager entityManager;


    @Override
    public DepartureDetailResponse createDeparture(CreateDepartureRequest request) {
        log.info("Creating new departure for tour ID: {} on date: {}",
                request.getTourId(), request.getDepartureDate());

        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tour với ID: " + request.getTourId()));

        if (departureRepository.existsByTourAndDepartureDate(tour, request.getDepartureDate())) {
            throw new DuplicateResourceException(
                    "Lịch khởi hành cho ngày " + request.getDepartureDate() + " đã tồn tại");
        }

        PolicyTemplate policyTemplate = policyTemplateRepository
                .findById(request.getPolicyTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy mẫu chính sách với ID: " + request.getPolicyTemplateId()));

        Coupon coupon = null;
        if (request.getCouponId() != null) {
            coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy coupon với ID: " + request.getCouponId()));
        }

        // Create departure entity
        TourDeparture departure = new TourDeparture();
        departure.setTour(tour);
        departure.setDepartureDate(request.getDepartureDate());
        departure.setAvailableSlots(request.getAvailableSlots());
        departure.setTourGuideInfo(request.getTourGuideInfo());
        departure.setPolicyTemplate(policyTemplate);
        departure.setCoupon(coupon);
        departure.setStatus(true);

        departure = departureRepository.save(departure);
        log.info("Departure entity created with ID: {}", departure.getDepartureID());

        // Save pricings
        if (request.getPricings() != null && !request.getPricings().isEmpty()) {
            savePricings(departure, request.getPricings());
        }

        // Save transports
        if (request.getOutboundTransport() != null) {
            saveTransport(departure, request.getOutboundTransport());
        }
        if (request.getInboundTransport() != null) {
            saveTransport(departure, request.getInboundTransport());
        }

        entityManager.flush();
        entityManager.clear();

        departure = departureRepository.findById(departure.getDepartureID())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departure not found after creation"));

        log.info("Departure created successfully with ID: {}", departure.getDepartureID());
        return mapToDepartureDetailResponse(departure);
    }

    @Override
    public DepartureDetailResponse updateDeparture(Integer departureId, UpdateDepartureRequest request) {
        log.info("Updating departure with ID: {}", departureId);

        TourDeparture departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch khởi hành với ID: " + departureId));

        // Update basic fields
        if (request.getDepartureDate() != null) {
            // Check duplicate if changing date
            if (!departure.getDepartureDate().equals(request.getDepartureDate())) {
                if (departureRepository.existsByTourAndDepartureDate(
                        departure.getTour(), request.getDepartureDate())) {
                    throw new DuplicateResourceException(
                            "Lịch khởi hành cho ngày " + request.getDepartureDate() + " đã tồn tại");
                }
                departure.setDepartureDate(request.getDepartureDate());
            }
        }

        if (request.getAvailableSlots() != null) {
            departure.setAvailableSlots(request.getAvailableSlots());
        }

        if (request.getTourGuideInfo() != null) {
            departure.setTourGuideInfo(request.getTourGuideInfo());
        }

        if (request.getStatus() != null) {
            departure.setStatus(request.getStatus());
        }

        if (request.getPolicyTemplateId() != null) {
            PolicyTemplate policyTemplate = policyTemplateRepository
                    .findById(request.getPolicyTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy mẫu chính sách với ID: " + request.getPolicyTemplateId()));
            departure.setPolicyTemplate(policyTemplate);
        }

        if (request.getCouponId() != null) {
            Coupon coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy coupon với ID: " + request.getCouponId()));
            departure.setCoupon(coupon);
        }

        // Update pricings if provided
        if (request.getPricings() != null) {
            pricingRepository.deleteByTourDeparture(departure);
            entityManager.flush();
            savePricings(departure, request.getPricings());
        }

        // Update transports if provided
        if (request.getOutboundTransport() != null || request.getInboundTransport() != null) {
            transportRepository.deleteByTourDeparture(departure);
            entityManager.flush();

            if (request.getOutboundTransport() != null) {
                saveTransport(departure, request.getOutboundTransport());
            }
            if (request.getInboundTransport() != null) {
                saveTransport(departure, request.getInboundTransport());
            }
        }

        departure = departureRepository.save(departure);
        entityManager.flush();
        entityManager.clear();

        departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departure not found after update"));

        log.info("Departure updated successfully with ID: {}", departureId);
        return mapToDepartureDetailResponse(departure);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartureDetailResponse getDepartureById(Integer departureId) {
        log.info("Fetching departure with ID: {}", departureId);

        TourDeparture departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch khởi hành với ID: " + departureId));

        return mapToDepartureDetailResponse(departure);
    }

    @Override
    public Page<DepartureSummaryResponse> getAllDepartures(Integer tourId, LocalDate startDate, LocalDate endDate, Boolean status, Pageable pageable) {
        log.info("Fetching departures with filters - tourId: {}, startDate: {}, endDate: {}, status: {}",
                tourId, startDate, endDate, status);

        Page<TourDeparture> departures = departureRepository
                .findWithFilters(tourId, startDate, endDate, status, pageable);

        return departures.map(this::mapToDepartureSummaryResponse);

    }

    @Override
    public List<DepartureSummaryResponse> getDeparturesByTourId(Integer tourId, Boolean status) {
        log.info("Fetching departures for tour ID: {}, status: {}", tourId, status);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tour với ID: " + tourId));

        List<TourDeparture> departures;
        if (status != null) {
            departures = departureRepository.findByTourAndStatus(tour, status);
        } else {
            departures = departureRepository.findByTour(tour);
        }

        return departures.stream()
                .map(this::mapToDepartureSummaryResponse)
                .sorted(Comparator.comparing(DepartureSummaryResponse::getDepartureDate))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDeparture(Integer departureId) {
        log.info("Deleting departure with ID: {}", departureId);

        TourDeparture departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch khởi hành với ID: " + departureId));

        // Soft delete
        departure.setStatus(false);
        departureRepository.save(departure);

        log.info("Departure soft deleted with ID: {}", departureId);
    }

    @Override
    public DepartureDetailResponse updatePricing(Integer departureId, List<DeparturePricingRequest> pricings) {
        log.info("Updating pricing for departure ID: {}", departureId);

        TourDeparture departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch khởi hành với ID: " + departureId));

        pricingRepository.deleteByTourDeparture(departure);
        entityManager.flush();

        savePricings(departure, pricings);

        entityManager.flush();
        entityManager.clear();

        departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departure not found after pricing update"));

        log.info("Pricing updated successfully for departure ID: {}", departureId);
        return mapToDepartureDetailResponse(departure);
    }

    @Override
    public DepartureDetailResponse updateTransport(Integer departureId, String direction, DepartureTransportRequest transport) {
        log.info("Updating {} transport for departure ID: {}", direction, departureId);

        TourDeparture departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch khởi hành với ID: " + departureId));

        // Delete only the specific direction transport
        // This is a simplified approach - you might need more sophisticated logic
        transportRepository.deleteByTourDeparture(departure);
        entityManager.flush();

        saveTransport(departure, transport);

        entityManager.flush();
        entityManager.clear();

        departure = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departure not found after transport update"));

        log.info("Transport updated successfully for departure ID: {}", departureId);
        return mapToDepartureDetailResponse(departure);
    }

    @Override
    public DepartureDetailResponse cloneDeparture(Integer departureId, LocalDate newDepartureDate) {
        log.info("Cloning departure ID: {} to new date: {}", departureId, newDepartureDate);

        TourDeparture originalDeparture = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy lịch khởi hành với ID: " + departureId));

        // Check duplicate
        if (departureRepository.existsByTourAndDepartureDate(
                originalDeparture.getTour(), newDepartureDate)) {
            throw new DuplicateResourceException(
                    "Lịch khởi hành cho ngày " + newDepartureDate + " đã tồn tại");
        }

        // Clone departure
        TourDeparture newDeparture = new TourDeparture();
        newDeparture.setTour(originalDeparture.getTour());
        newDeparture.setDepartureDate(newDepartureDate);
        newDeparture.setAvailableSlots(originalDeparture.getAvailableSlots());
        newDeparture.setTourGuideInfo(originalDeparture.getTourGuideInfo());
        newDeparture.setPolicyTemplate(originalDeparture.getPolicyTemplate());
        newDeparture.setCoupon(originalDeparture.getCoupon());
        newDeparture.setStatus(true);

        newDeparture = departureRepository.save(newDeparture);

        // Clone pricings
        for (DeparturePricing originalPricing : originalDeparture.getPricings()) {
            DeparturePricing newPricing = new DeparturePricing();
            newPricing.setTourDeparture(newDeparture);
            newPricing.setPassengerType(originalPricing.getPassengerType());
            newPricing.setAgeDescription(originalPricing.getAgeDescription());
            newPricing.setOriginalPrice(originalPricing.getOriginalPrice());
            newPricing.setSalePrice(originalPricing.getSalePrice());
            pricingRepository.save(newPricing);
        }

        // Clone transports
        for (DepartureTransport originalTransport : originalDeparture.getTransports()) {
            DepartureTransport newTransport = new DepartureTransport();
            newTransport.setTourDeparture(newDeparture);
            newTransport.setType(originalTransport.getType());
            newTransport.setTransportCode(originalTransport.getTransportCode());
            newTransport.setVehicleName(originalTransport.getVehicleName());
            newTransport.setStartPoint(originalTransport.getStartPoint());
            newTransport.setEndPoint(originalTransport.getEndPoint());
            newTransport.setDepartTime(originalTransport.getDepartTime());
            newTransport.setArrivalTime(originalTransport.getArrivalTime());
            transportRepository.save(newTransport);
        }

        entityManager.flush();
        entityManager.clear();

        newDeparture = departureRepository.findById(newDeparture.getDepartureID())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Departure not found after cloning"));

        log.info("Departure cloned successfully with new ID: {}", newDeparture.getDepartureID());
        return mapToDepartureDetailResponse(newDeparture);
    }

    private void savePricings(TourDeparture departure, List<DeparturePricingRequest> pricingRequests) {
        for (DeparturePricingRequest req : pricingRequests) {
            DeparturePricing pricing = new DeparturePricing();
            pricing.setTourDeparture(departure);
            pricing.setPassengerType(req.getPassengerType());

            // Auto-generate age description if not provided
            String ageDesc = req.getAgeDescription();
            if (ageDesc == null || ageDesc.trim().isEmpty()) {
                ageDesc = req.getPassengerType().name();
            }
            pricing.setAgeDescription(ageDesc);

            pricing.setOriginalPrice(req.getOriginalPrice());

            // If salePrice is null, use originalPrice
            BigDecimal salePrice = req.getSalePrice();
            if (salePrice == null) {
                salePrice = req.getOriginalPrice();
            }
            pricing.setSalePrice(salePrice);

            pricingRepository.save(pricing);
        }
    }

    private void saveTransport(TourDeparture departure, DepartureTransportRequest transportRequest) {
        DepartureTransport transport = new DepartureTransport();
        transport.setTourDeparture(departure);
        transport.setType(transportRequest.getType());
        transport.setTransportCode(transportRequest.getTransportCode());
        transport.setVehicleTyle(transportRequest.getVehicleType());
        transport.setVehicleName(transportRequest.getVehicleName());
        transport.setStartPoint(transportRequest.getStartPoint());
        transport.setEndPoint(transportRequest.getEndPoint());
        transport.setDepartTime(transportRequest.getDepartTime());
        transport.setArrivalTime(transportRequest.getArrivalTime());

        transportRepository.save(transport);
    }

    private DepartureDetailResponse mapToDepartureDetailResponse(TourDeparture departure) {
        Tour tour = departure.getTour();

        // Separate transports by direction (assuming first is outbound, second is inbound)
        List<DepartureTransport> transports = departure.getTransports();
        DepartureTransportResponse outbound = null;
        DepartureTransportResponse inbound = null;

        if (transports != null && !transports.isEmpty()) {
            if (transports.size() >= 1) {
                outbound = mapToTransportResponse(transports.get(0));
            }
            if (transports.size() >= 2) {
                inbound = mapToTransportResponse(transports.get(1));
            }
        }

        return DepartureDetailResponse.builder()
                .departureID(departure.getDepartureID())
                .departureDate(departure.getDepartureDate())
                .status(departure.getStatus())
                .availableSlots(departure.getAvailableSlots())
                .tourGuideInfo(departure.getTourGuideInfo())
                .tourId(tour.getTourID())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .policyTemplateId(departure.getPolicyTemplate().getPolicyTemplateID())
                .policyTemplateName(departure.getPolicyTemplate().getTemplateName())
                .couponId(departure.getCoupon() != null ? departure.getCoupon().getCouponID() : null)
                .couponCode(departure.getCoupon() != null ? departure.getCoupon().getCouponCode() : null)
                .pricings(mapToPricingResponses(departure.getPricings()))
                .outboundTransport(outbound)
                .inboundTransport(inbound)
                .createdAt(departure.getCreatedAt())
                .updatedAt(departure.getUpdatedAt())
                .build();
    }

    private DepartureSummaryResponse mapToDepartureSummaryResponse(TourDeparture departure) {
        Tour tour = departure.getTour();

        // Get lowest price
        BigDecimal lowestPrice = departure.getPricings().stream()
                .map(DeparturePricing::getSalePrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        Integer bookedSlots = 0;
        if (departure.getBookings() != null) {
            bookedSlots = departure.getBookings().stream()
                    .filter(booking -> booking.getBookingStatus() != null
                            && !booking.getBookingStatus().equals("CANCELLED"))
                    .mapToInt(booking -> booking.getTotalPassengers() != null
                            ? booking.getTotalPassengers()
                            : 0)
                    .sum();
        }

        // Check if transports exist
        List<DepartureTransport> transports = departure.getTransports();
        boolean hasOutbound = transports != null && transports.stream()
                .anyMatch(t -> t.getType() == TransportType.OUTBOUND);
        boolean hasInbound = transports != null && transports.stream()
                .anyMatch(t -> t.getType() == TransportType.INBOUND);

        return DepartureSummaryResponse.builder()
                .departureID(departure.getDepartureID())
                .departureDate(departure.getDepartureDate())
                .status(departure.getStatus())
                .availableSlots(departure.getAvailableSlots())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .tourDuration(tour.getDuration())
                .lowestPrice(lowestPrice)
                .totalBookings(bookedSlots)
                .hasOutboundTransport(hasOutbound)
                .hasInboundTransport(hasInbound)
                .build();
    }

    private List<DeparturePricingResponse> mapToPricingResponses(List<DeparturePricing> pricings) {
        if (pricings == null || pricings.isEmpty()) {
            return new ArrayList<>();
        }

        return pricings.stream()
                .map(p -> DeparturePricingResponse.builder()
                        .pricingID(p.getPricingID())
                        .passengerType(p.getPassengerType())
                        .ageDescription(p.getAgeDescription())
                        .originalPrice(p.getOriginalPrice())
                        .salePrice(p.getSalePrice())
                        .build())
                .collect(Collectors.toList());
    }

    private DepartureTransportResponse mapToTransportResponse(DepartureTransport transport) {
        if (transport == null) {
            return null;
        }
        return DepartureTransportResponse.builder()
                .transportID(transport.getTransportID())
                .type(transport.getType())
                .transportCode(transport.getTransportCode())
                .vehicleName(transport.getVehicleName())
                .startPoint(transport.getStartPoint())
                .endPoint(transport.getEndPoint())
                .departTime(transport.getDepartTime())
                .arrivalTime(transport.getArrivalTime())
                .build();
    }
}
