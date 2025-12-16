package com.tourism.backend.controller;

import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.requestDTO.RegionRequestDTO;

import com.tourism.backend.dto.requestDTO.SearchToursRequestDTO;

import com.tourism.backend.dto.response.DepartureSimpleResponse;
import com.tourism.backend.dto.response.TourCardResponseDTO;
import com.tourism.backend.dto.response.TourDetailResponseDTO;

import com.tourism.backend.dto.response.TourSimpleResponse;
import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.ErrorResponseDTO;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.dto.responseDTO.TourSpecialResponseDTO;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    @GetMapping("/deepest-discount")
    public ResponseEntity<?> getTop10DeepestDiscountTours() {
        try {
            List<TourSpecialResponseDTO> tours = tourService.getTop10DeepestDiscountTours();
            return ResponseEntity.ok(tours);

        } catch (Exception e) {
            // 1. Log lỗi ra console/file log
            System.err.println("Lỗi khi lấy danh sách tour giảm giá sâu nhất: " + e.getMessage());

            // 2. Tạo đối tượng ErrorResponseDTO với HTTP Status 500
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "Không thể xử lý yêu cầu lấy tour giảm giá. Chi tiết: " + e.getMessage()
            );

            // 3. Trả về phản hồi 500 kèm theo ErrorResponseDTO
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTours(
            @ModelAttribute SearchToursRequestDTO dto,
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        try {
            // DTO chứa tất cả thông tin tìm kiếm
            List<TourResponseDTO> tours = tourService.searchTours(dto,userId);

            if (tours.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("Không tìm thấy tour nào phù hợp với điều kiện lọc.");
            }

            return ResponseEntity.ok(tours);
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Error",
                    "Lỗi khi thực hiện tìm kiếm tour: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{tourCode}")
    public ResponseEntity<TourDetailResponseDTO> getTourDetail(@PathVariable String tourCode){
        TourDetailResponseDTO responseDTO = tourService.getTourDetail(tourCode);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/related/{tourCode}")
    public ResponseEntity<List<TourCardResponseDTO>> getRelatedTours(@PathVariable String tourCode) {
        return ResponseEntity.ok(tourService.getRelatedTours(tourCode));
    }

    @GetMapping
    public ResponseEntity<Page<TourSimpleResponse>> getAllTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(tourService.getAllToursSimple(pageable));
    }

    @GetMapping("/{id}/departures")
    public ResponseEntity<List<DepartureSimpleResponse>> getTourDepartures(@PathVariable("id") Integer tourId) {
        return ResponseEntity.ok(tourService.getDeparturesByTour(tourId));
    }

}
