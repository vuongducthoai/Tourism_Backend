package com.tourism.backend.service;

import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.enums.Region;

import java.io.IOException;
import java.util.List;

public interface TourService {
    Tour createTourWithImages(TourCreateDTO dto) throws IOException;

    List<Tour> getAllTours();
    Tour getTourByCode(String tourCode);

    // Phương thức mới để trả về List DTO
    List<TourResponseDTO> getAllToursForListDisplay();
    // ✨ PHƯƠNG THỨC MỚI: Lấy danh sách điểm đến theo Region, trả về DTO
    List<DestinationResponseDTO> getFavoriteDestinationsByRegion(Region region);
}