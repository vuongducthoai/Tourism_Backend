package com.tourism.backend.repository.custom;

import com.tourism.backend.dto.requestDTO.SearchToursRequestDTO;
import com.tourism.backend.entity.Tour;

import java.util.List;

public interface TourRepositoryCustom {
    List<Tour> searchToursDynamically(SearchToursRequestDTO dto);
}