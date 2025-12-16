package com.tourism.backend.service;

import com.tourism.backend.dto.TourCreateDTO;
import com.tourism.backend.dto.requestDTO.SearchToursRequestDTO;
import com.tourism.backend.dto.response.DepartureSimpleResponse;
import com.tourism.backend.dto.response.TourCardResponseDTO;
import com.tourism.backend.dto.response.TourDetailResponseDTO;
import com.tourism.backend.dto.response.TourSimpleResponse;
import com.tourism.backend.dto.responseDTO.TourResponseDTO;
import com.tourism.backend.dto.responseDTO.TourSpecialResponseDTO;
import com.tourism.backend.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface TourService {
    Tour createTourWithImages(TourCreateDTO dto) throws IOException;

    List<Tour> getAllTours();
    Tour getTourByCode(String tourCode);
    List<TourResponseDTO> getAllToursForListDisplay();
    TourDetailResponseDTO getTourDetail(String tourCode);
    List<TourSpecialResponseDTO> getTop10DeepestDiscountTours();
    List<TourResponseDTO> searchTours(SearchToursRequestDTO dto,Integer userId);
    List<TourCardResponseDTO> getRelatedTours(String currentTourCode);
    Page<TourSimpleResponse> getAllToursSimple(Pageable pageable);
    List<DepartureSimpleResponse> getDeparturesByTour(Integer tourId);
}