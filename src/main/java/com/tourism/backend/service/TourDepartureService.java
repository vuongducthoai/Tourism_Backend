package com.tourism.backend.service;

import com.tourism.backend.dto.request.CreateDepartureRequest;
import com.tourism.backend.dto.request.DeparturePricingRequest;
import com.tourism.backend.dto.request.DepartureTransportRequest;
import com.tourism.backend.dto.request.UpdateDepartureRequest;
import com.tourism.backend.dto.response.DepartureDetailResponse;
import com.tourism.backend.dto.response.DepartureSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TourDepartureService {
    DepartureDetailResponse createDeparture(CreateDepartureRequest request);

    DepartureDetailResponse updateDeparture(Integer departureId, UpdateDepartureRequest request);

    DepartureDetailResponse getDepartureById(Integer departureId);

    Page<DepartureSummaryResponse> getAllDepartures(
            Integer tourId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean status,
            Pageable pageable);

    List<DepartureSummaryResponse> getDeparturesByTourId(Integer tourId, Boolean status);

    void deleteDeparture(Integer departureId);

    DepartureDetailResponse updatePricing(Integer departureId, List<DeparturePricingRequest> pricings);

    DepartureDetailResponse updateTransport(Integer departureId, String direction, DepartureTransportRequest transport);

    DepartureDetailResponse cloneDeparture(Integer departureId, LocalDateTime newDepartureDate);
}
