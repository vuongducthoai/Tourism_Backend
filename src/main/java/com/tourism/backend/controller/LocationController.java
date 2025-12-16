package com.tourism.backend.controller;

import com.tourism.backend.dto.requestDTO.RegionRequestDTO;
import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.dto.responseDTO.LocationResponseDTO;
import com.tourism.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/destinations-by-region")
    public ResponseEntity<?> getUniqueDestinationsByRegion(@RequestBody RegionRequestDTO dto) {
        try {
            if (dto.getRegion() == null) {
                return ResponseEntity.badRequest().body("Region parameter is required.");
            }
            List<DestinationResponseDTO> responseDTOs =
                    locationService.getUniqueEndDestinationsByRegion(dto.getRegion());

            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi server khi tìm kiếm điểm đến theo region: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @GetMapping("/end-location")
    public ResponseEntity<?> getAllUniqueEndLocations() {
        try {
            List<LocationResponseDTO> locations = locationService.getAllUniqueEndLocations();
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách điểm đến cuối duy nhất: " + e.getMessage());
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Không thể lấy danh sách điểm đến. Chi tiết: " + e.getMessage()
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
    @GetMapping("/start-location")
    public ResponseEntity<?> getAllUniqueStartLocations() {
        try {
            List<LocationResponseDTO> locations = locationService.getAllUniqueStartLocations();
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách điểm đến cuối duy nhất: " + e.getMessage());
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Không thể lấy danh sách điểm đến. Chi tiết: " + e.getMessage()
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}