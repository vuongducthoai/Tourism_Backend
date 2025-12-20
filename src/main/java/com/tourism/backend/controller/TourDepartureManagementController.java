package com.tourism.backend.controller;

import com.tourism.backend.dto.request.CreateDepartureRequest;
import com.tourism.backend.dto.request.DeparturePricingRequest;
import com.tourism.backend.dto.request.DepartureTransportRequest;
import com.tourism.backend.dto.request.UpdateDepartureRequest;
import com.tourism.backend.dto.response.DepartureDetailResponse;
import com.tourism.backend.dto.response.DepartureSummaryResponse;
import com.tourism.backend.service.TourDepartureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/departures")
@RequiredArgsConstructor
public class TourDepartureManagementController {
    private final TourDepartureService departureService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDeparture(
            @Valid @RequestBody CreateDepartureRequest request) {

        DepartureDetailResponse response = departureService.createDeparture(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Tạo lịch khởi hành thành công");
        result.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{departureId}")
    public ResponseEntity<Map<String, Object>> updateDeparture(
            @PathVariable Integer departureId,
            @Valid @RequestBody UpdateDepartureRequest request) {

        DepartureDetailResponse response = departureService.updateDeparture(departureId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Cập nhật lịch khởi hành thành công");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{departureId}")
    public ResponseEntity<Map<String, Object>> getDepartureById(
            @PathVariable Integer departureId) {

        DepartureDetailResponse response = departureService.getDepartureById(departureId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }



    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDepartures(
            @RequestParam(required = false) Integer tourId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateTime = endDate.atTime(23, 59, 59);
        }
        Page<DepartureSummaryResponse> departures = departureService.getAllDepartures(
                tourId, startDateTime, endDateTime, status, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", departures.getContent());
        result.put("currentPage", departures.getNumber());
        result.put("totalItems", departures.getTotalElements());
        result.put("totalPages", departures.getTotalPages());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-tour/{tourId}")
    public ResponseEntity<Map<String, Object>> getDeparturesByTour(
            @PathVariable Integer tourId,
            @RequestParam(required = false) Boolean status) {

        List<DepartureSummaryResponse> departures =
                departureService.getDeparturesByTourId(tourId, status);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", departures);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{departureId}")
    public ResponseEntity<Map<String, Object>> deleteDeparture(
            @PathVariable Integer departureId) {

        departureService.deleteDeparture(departureId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Xóa lịch khởi hành thành công");

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{departureId}/pricing")
    public ResponseEntity<Map<String, Object>> updatePricing(
            @PathVariable Integer departureId,
            @Valid @RequestBody List<DeparturePricingRequest> pricings) {

        DepartureDetailResponse response =
                departureService.updatePricing(departureId, pricings);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Cập nhật giá thành công");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{departureId}/transport")
    public ResponseEntity<Map<String, Object>> updateTransport(
            @PathVariable Integer departureId,
            @RequestParam String direction,
            @Valid @RequestBody DepartureTransportRequest transport) {

        DepartureDetailResponse response =
                departureService.updateTransport(departureId, direction, transport);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Cập nhật vận chuyển thành công");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{departureId}/clone")
    public ResponseEntity<Map<String, Object>> cloneDeparture(
            @PathVariable Integer departureId,
            @RequestParam LocalDateTime newDepartureDate) {

        DepartureDetailResponse response =
                departureService.cloneDeparture(departureId, newDepartureDate);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Sao chép lịch khởi hành thành công");
        result.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
