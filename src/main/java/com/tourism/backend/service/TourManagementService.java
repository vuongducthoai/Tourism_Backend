package com.tourism.backend.service;

import com.tourism.backend.dto.request.*;
import com.tourism.backend.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TourManagementService {
    TourDetailResponse createTour(CreateTourRequest request);
    TourDetailResponse updateTour(Integer tourId, UpdateTourRequest request);
    TourDetailResponse getTourById(Integer tourId);
    Page<TourDetailResponse> getAllTours(Pageable pageable);
    void deleteTour(Integer tourId);

    TourDetailResponse updateGeneralInfo(Integer tourId, TourGeneralInfoRequest request);
    TourDetailResponse updateImages(Integer tourId, List<TourImageRequest> images);
    TourDetailResponse updateMedia(Integer tourId, List<TourMediaRequest> mediaList);
    TourDetailResponse updateItinerary(Integer tourId, List<ItineraryDayRequest> itineraryDays);

    List<LocationResponse> getAllLocations();
    boolean tourCodeExists(String tourCode);
    TourDetailResponse getTourByCode(String tourCode);
}