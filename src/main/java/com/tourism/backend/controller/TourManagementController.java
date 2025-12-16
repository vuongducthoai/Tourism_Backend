package com.tourism.backend.controller;

import com.tourism.backend.dto.request.*;
import com.tourism.backend.dto.response.LocationResponse;
import com.tourism.backend.dto.response.TourDetailResponse;
import com.tourism.backend.service.TourManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tours")
@RequiredArgsConstructor
public class TourManagementController {
    private final TourManagementService tourManagementService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTour(@Valid @RequestBody CreateTourRequest request) {
        TourDetailResponse response = tourManagementService.createTour(request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Tạo tour thành công");
        result.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<Map<String, Object>> updateTour(
            @PathVariable Integer tourId,
            @Valid @RequestBody UpdateTourRequest request) {

        TourDetailResponse response = tourManagementService.updateTour(tourId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Cập nhật tour thành công");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<Map<String, Object>> getTourById(@PathVariable Integer tourId) {
        TourDetailResponse response = tourManagementService.getTourById(tourId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "tourID") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<TourDetailResponse> tours = tourManagementService.getAllTours(pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", tours.getContent());
        result.put("currentPage", tours.getNumber());
        result.put("totalItems", tours.getTotalElements());
        result.put("totalPages", tours.getTotalPages());

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Map<String, Object>> deleteTour(@PathVariable Integer tourId) {
        tourManagementService.deleteTour(tourId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Xóa tour thành công");

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{tourId}/general-info")
    public ResponseEntity<Map<String, Object>> updateGeneralInfo(
            @PathVariable Integer tourId,
            @Valid @RequestBody TourGeneralInfoRequest request) {

        TourDetailResponse response = tourManagementService.updateGeneralInfo(tourId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Cập nhật thông tin chung thành công");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{tourId}/itinerary")
    public ResponseEntity<Map<String, Object>> updateItinerary(
            @PathVariable Integer tourId,
            @Valid @RequestBody List<ItineraryDayRequest> itineraryDays) {

        TourDetailResponse response = tourManagementService.updateItinerary(tourId, itineraryDays);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Cập nhật lịch trình thành công");
        result.put("data", response);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/locations")
    public ResponseEntity<Map<String, Object>> getAllLocations() {
        List<LocationResponse> locations = tourManagementService.getAllLocations();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", locations);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Object>> checkTourCode(@RequestParam String tourCode) {
        boolean exists = tourManagementService.tourCodeExists(tourCode);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("exists", exists);
        result.put("message", exists ? "Mã tour đã tồn tại" : "Mã tour có thể sử dụng");

        return ResponseEntity.ok(result);
    }
}
