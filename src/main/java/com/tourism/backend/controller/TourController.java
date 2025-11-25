package com.tourism.backend.controller;

import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {
    private final TourService tourService;

    @PostMapping(value = "/create-with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createTour(@ModelAttribute TourCreateDTO tourDTO) {
        try {
            Tour newTour = tourService.createTourWithImages(tourDTO);
            return ResponseEntity.ok(newTour);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Lỗi upload ảnh: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tạo tour: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }

    @GetMapping("/{tourCode}")
    public ResponseEntity<?> getTourByCode(@PathVariable String tourCode) {
        try {
            Tour tour = tourService.getTourByCode(tourCode);
            return ResponseEntity.ok(tour);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    /**
     * API mới: Lấy tất cả các Tour theo định dạng TourReponsetory DTO.
     * HTTP GET /api/tours/display
     */
    @GetMapping("/display")
    public ResponseEntity<?> getAllToursForDisplay() {
        try {
            List<TourResponseDTO> tours = tourService.getAllToursForListDisplay();
            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi lấy danh sách tour: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
