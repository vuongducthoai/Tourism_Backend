package com.tourism.backend.service;

import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.response.TourDetailResponseDTO;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.entity.Tour;
import java.io.IOException;
import java.util.List;

public interface TourService {
    Tour createTourWithImages(TourCreateDTO dto) throws IOException;

    List<Tour> getAllTours();
    Tour getTourByCode(String tourCode);

    // Phương thức mới để trả về List DTO
    List<TourResponseDTO> getAllToursForListDisplay();

    TourDetailResponseDTO getTourDetail(String tourCode);
}