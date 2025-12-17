package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.*;
import com.tourism.backend.dto.response.*;
import com.tourism.backend.entity.*;
import com.tourism.backend.enums.Region;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TourManagementServiceImpl implements TourManagementService {
    private final TourRepository tourRepository;
    private final LocationRepository locationRepository;
    private final TourImageRepository tourImageRepository;
    private final TourMediaRepository tourMediaRepository;
    private final ItineraryDayRepository itineraryDayRepository;
    private final EntityManager entityManager; // FIX: Thêm EntityManager để flush

    @Override
    public TourDetailResponse createTour(CreateTourRequest request) {
        log.info("Creating new tour with code: {}", request.getGeneralInfo().getTourCode());

        String tourCode = request.getGeneralInfo().getTourCode().trim().toUpperCase();
        if (tourRepository.existsByTourCode(tourCode)) {
            throw new DuplicateResourceException("Mã tour đã tồn tại: " + tourCode);
        }

        Tour tour = mapToTourEntity(request.getGeneralInfo());
        tour = tourRepository.save(tour);
        log.info("Tour entity created with ID: {}", tour.getTourID());

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            saveImages(tour, request.getImages());
            log.info("Saved {} images for tour {}", request.getImages().size(), tour.getTourID());
        }

        if (request.getMediaList() != null && !request.getMediaList().isEmpty()) {
            saveMedia(tour, request.getMediaList());
            log.info("Saved {} media items for tour {}", request.getMediaList().size(), tour.getTourID());
        }

        if (request.getItineraryDays() != null && !request.getItineraryDays().isEmpty()) {
            saveItinerary(tour, request.getItineraryDays());
            log.info("Saved {} itinerary days for tour {}", request.getItineraryDays().size(), tour.getTourID());
        }

        // FIX: Flush và refresh để đảm bảo data mới nhất
        entityManager.flush();
        entityManager.clear();

        tour = tourRepository.findById(tour.getTourID())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found after creation"));

        log.info("Tour created successfully with ID: {}", tour.getTourID());
        return mapToTourDetailResponse(tour);
    }

    @Override
    public TourDetailResponse updateTour(Integer tourId, UpdateTourRequest request) {
        log.info("Updating tour with ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        if (request.getGeneralInfo() != null) {
            updateTourGeneralInfo(tour, request.getGeneralInfo());
            log.info("Updated general info for tour {}", tourId);
        }

        if (request.getImages() != null) {
            tourImageRepository.deleteByTour(tour);
            entityManager.flush(); // FIX: Flush sau khi delete
            if (!request.getImages().isEmpty()) {
                saveImages(tour, request.getImages());
            }
            log.info("Updated images for tour {}", tourId);
        }

        if (request.getMediaList() != null) {
            tourMediaRepository.deleteByTour(tour);
            entityManager.flush(); // FIX: Flush sau khi delete
            if (!request.getMediaList().isEmpty()) {
                saveMedia(tour, request.getMediaList());
            }
            log.info("Updated media for tour {}", tourId);
        }

        if (request.getItineraryDays() != null) {
            itineraryDayRepository.deleteByTour(tour);
            entityManager.flush(); // FIX: Flush sau khi delete
            if (!request.getItineraryDays().isEmpty()) {
                saveItinerary(tour, request.getItineraryDays());
            }
            log.info("Updated itinerary for tour {}", tourId);
        }

        tour = tourRepository.save(tour);
        entityManager.flush();
        entityManager.clear(); // FIX: Clear cache

        tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found after update"));

        log.info("Tour updated successfully with ID: {}", tourId);
        return mapToTourDetailResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailResponse getTourById(Integer tourId) {
        log.info("Fetching tour with ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        return mapToTourDetailResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailResponse getTourByCode(String tourCode) {
        log.info("Fetching tour with code: {}", tourCode);

        String normalizedCode = tourCode.trim().toUpperCase();
        Tour tour = tourRepository.findByTourCode(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với mã: " + tourCode));

        return mapToTourDetailResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TourDetailResponse> getAllTours(Pageable pageable) {
        log.info("Fetching tours with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Tour> tours = tourRepository.findAll(pageable);

        return tours.map(this::mapToTourDetailResponse);
    }

    @Override
    public void deleteTour(Integer tourId) {
        log.info("Deleting tour with ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        tourRepository.delete(tour);

        log.info("Tour deleted successfully with ID: {}", tourId);
    }

    @Override
    public TourDetailResponse updateGeneralInfo(Integer tourId, TourGeneralInfoRequest request) {
        log.info("Updating general info for tour ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        updateTourGeneralInfo(tour, request);
        tour = tourRepository.save(tour);

        log.info("General info updated successfully for tour ID: {}", tourId);
        return mapToTourDetailResponse(tour);
    }

    @Override
    public TourDetailResponse updateImages(Integer tourId, List<TourImageRequest> images) {
        log.info("Updating images for tour ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        tourImageRepository.deleteByTour(tour);
        entityManager.flush(); // FIX: Flush ngay sau delete
        log.info("Deleted existing images for tour ID: {}", tourId);

        if (images != null && !images.isEmpty()) {
            saveImages(tour, images);
            log.info("Saved {} new images for tour ID: {}", images.size(), tourId);
        }

        entityManager.flush();
        entityManager.clear(); // FIX: Clear cache

        tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found after image update"));

        return mapToTourDetailResponse(tour);
    }

    @Override
    public TourDetailResponse updateMedia(Integer tourId, List<TourMediaRequest> mediaList) {
        log.info("Updating media for tour ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        tourMediaRepository.deleteByTour(tour);
        entityManager.flush(); // FIX: Flush ngay sau delete
        log.info("Deleted existing media for tour ID: {}", tourId);

        if (mediaList != null && !mediaList.isEmpty()) {
            saveMedia(tour, mediaList);
            log.info("Saved {} new media items for tour ID: {}", mediaList.size(), tourId);
        }

        entityManager.flush();
        entityManager.clear(); // FIX: Clear cache

        tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found after media update"));

        return mapToTourDetailResponse(tour);
    }

    @Override
    public TourDetailResponse updateItinerary(Integer tourId, List<ItineraryDayRequest> itineraryDays) {
        log.info("Updating itinerary for tour ID: {}", tourId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + tourId));

        // FIX: Delete existing days và flush ngay lập tức
        itineraryDayRepository.deleteByTour(tour);
        entityManager.flush(); // CRITICAL: Phải flush để xóa hết data cũ trước khi insert mới
        log.info("Deleted existing itinerary days for tour ID: {}", tourId);

        if (itineraryDays != null && !itineraryDays.isEmpty()) {
            // FIX: Validate và normalize dayNumber
            List<ItineraryDayRequest> normalizedDays = new ArrayList<>();
            for (int i = 0; i < itineraryDays.size(); i++) {
                ItineraryDayRequest day = itineraryDays.get(i);
                ItineraryDayRequest normalized = new ItineraryDayRequest();
                normalized.setDayNumber(i + 1); // Đảm bảo dayNumber liên tục
                normalized.setTitle(day.getTitle());
                normalized.setMeals(day.getMeals());
                normalized.setDetails(day.getDetails());
                normalizedDays.add(normalized);
            }

            saveItinerary(tour, normalizedDays);
            log.info("Saved {} new itinerary days for tour ID: {}", normalizedDays.size(), tourId);
        }

        entityManager.flush();
        entityManager.clear(); // FIX: Clear cache để reload data mới

        tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found after itinerary update"));

        return mapToTourDetailResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllLocations() {
        log.info("Fetching all active locations");

        List<Location> locations = locationRepository.findAllByStatusTrue();

        return locations.stream()
                .map(this::mapToLocationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tourCodeExists(String tourCode) {
        String normalizedCode = tourCode.trim().toUpperCase();
        boolean exists = tourRepository.existsByTourCode(normalizedCode);

        log.info("Checking if tour code exists: {} -> {}", normalizedCode, exists);
        return exists;
    }

    private Tour mapToTourEntity(TourGeneralInfoRequest request) {
        Tour tour = new Tour();
        tour.setTourCode(request.getTourCode().trim().toUpperCase());
        tour.setTourName(request.getTourName());
        tour.setDuration(request.getDuration());
        tour.setTransportation(request.getTransportation());
        tour.setAttractions(request.getAttractions());
        tour.setMeals(request.getMeals());
        tour.setIdealTime(request.getIdealTime());
        tour.setTripTransportation(request.getTripTransportation());
        tour.setSuitableCustomer(request.getSuitableCustomer());
        tour.setHotel(request.getHotel());
        tour.setStatus(request.getStatus() != null ? request.getStatus() : true);

        Location startLocation = locationRepository.findById(request.getStartLocationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy điểm khởi hành với ID: " + request.getStartLocationId()));

        Location endLocation = locationRepository.findById(request.getEndLocationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy điểm đến với ID: " + request.getEndLocationId()));

        tour.setStartLocation(startLocation);
        tour.setEndLocation(endLocation);

        return tour;
    }

    private void updateTourGeneralInfo(Tour tour, TourGeneralInfoRequest request) {
        String newTourCode = request.getTourCode().trim().toUpperCase();
        if (!tour.getTourCode().equals(newTourCode)) {
            if (tourRepository.existsByTourCode(newTourCode)) {
                throw new DuplicateResourceException("Mã tour đã tồn tại: " + newTourCode);
            }
            tour.setTourCode(newTourCode);
        }

        tour.setTourName(request.getTourName());
        tour.setDuration(request.getDuration());
        tour.setTransportation(request.getTransportation());
        tour.setAttractions(request.getAttractions());
        tour.setMeals(request.getMeals());
        tour.setIdealTime(request.getIdealTime());
        tour.setTripTransportation(request.getTripTransportation());
        tour.setSuitableCustomer(request.getSuitableCustomer());
        tour.setHotel(request.getHotel());

        if (request.getStatus() != null) {
            tour.setStatus(request.getStatus());
        }

        if (!tour.getStartLocation().getLocationID().equals(request.getStartLocationId())) {
            Location startLocation = locationRepository.findById(request.getStartLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy điểm khởi hành với ID: " + request.getStartLocationId()));
            tour.setStartLocation(startLocation);
        }

        if (!tour.getEndLocation().getLocationID().equals(request.getEndLocationId())) {
            Location endLocation = locationRepository.findById(request.getEndLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy điểm đến với ID: " + request.getEndLocationId()));
            tour.setEndLocation(endLocation);
        }
    }

    private void saveImages(Tour tour, List<TourImageRequest> imageRequests) {
        List<TourImage> images = new ArrayList<>();

        long mainImageCount = imageRequests.stream()
                .filter(req -> req.getIsMainImage() != null && req.getIsMainImage())
                .count();

        if (mainImageCount > 1) {
            log.warn("Multiple main images detected, only first one will be set as main");
        }

        boolean hasSetMainImage = false;

        for (TourImageRequest req : imageRequests) {
            TourImage image = new TourImage();
            image.setImageURL(req.getImageURL());

            if (req.getIsMainImage() != null && req.getIsMainImage() && !hasSetMainImage) {
                image.setIsMainImage(true);
                hasSetMainImage = true;
            } else {
                image.setIsMainImage(false);
            }

            image.setTour(tour);
            images.add(image);
        }

        tourImageRepository.saveAll(images);
    }

    private void saveMedia(Tour tour, List<TourMediaRequest> mediaRequests) {
        List<TourMedia> mediaList = new ArrayList<>();

        long primaryCount = mediaRequests.stream()
                .filter(req -> req.getIsPrimary() != null && req.getIsPrimary())
                .count();

        if (primaryCount > 1) {
            log.warn("Multiple primary media detected, only first one will be set as primary");
        }

        boolean hasSetPrimary = false;

        for (TourMediaRequest req : mediaRequests) {
            TourMedia media = TourMedia.builder()
                    .tour(tour)
                    .mediaUrl(req.getMediaUrl())
                    .thumbnailUrl(req.getThumbnailUrl())
                    .title(req.getTitle())
                    .description(req.getDescription())
                    .duration(req.getDuration())
                    .fileSize(req.getFileSize())
                    .isPrimary((req.getIsPrimary() != null && req.getIsPrimary() && !hasSetPrimary))
                    .build();

            if (media.getIsPrimary()) {
                hasSetPrimary = true;
            }

            mediaList.add(media);
        }

        tourMediaRepository.saveAll(mediaList);
    }

    private void saveItinerary(Tour tour, List<ItineraryDayRequest> itineraryRequests) {
        List<ItineraryDay> itineraryDays = new ArrayList<>();

        // FIX: Sort và validate dayNumber
        itineraryRequests.sort((a, b) -> a.getDayNumber().compareTo(b.getDayNumber()));

        for (ItineraryDayRequest req : itineraryRequests) {
            ItineraryDay day = new ItineraryDay();
            day.setDayNumber(req.getDayNumber());
            day.setTitle(req.getTitle());
            day.setMeals(req.getMeals());
            day.setDetails(req.getDetails());
            day.setTour(tour);
            itineraryDays.add(day);
        }

        itineraryDayRepository.saveAll(itineraryDays);
    }

    private TourDetailResponse mapToTourDetailResponse(Tour tour) {
        return TourDetailResponse.builder()
                .tourID(tour.getTourID())
                .tourCode(tour.getTourCode())
                .tourName(tour.getTourName())
                .duration(tour.getDuration())
                .transportation(tour.getTransportation())
                .startLocationId(tour.getStartLocation().getLocationID())
                .startLocationName(tour.getStartLocation().getName())
                .endLocationId(tour.getEndLocation().getLocationID())
                .endLocationName(tour.getEndLocation().getName())
                .attractions(tour.getAttractions())
                .meals(tour.getMeals())
                .idealTime(tour.getIdealTime())
                .tripTransportation(tour.getTripTransportation())
                .suitableCustomer(tour.getSuitableCustomer())
                .hotel(tour.getHotel())
                .status(tour.getStatus())
                .images(mapToImageResponses(tour.getImages()))
                .mediaList(mapToMediaResponses(tour.getMediaList()))
                .itineraryDays(mapToItineraryResponses(tour.getItineraryDays()))
                .createdAt(tour.getCreatedAt())
                .updatedAt(tour.getUpdatedAt())
                .createdBy(tour.getCreatedBy())
                .updatedBy(tour.getUpdatedBy())
                .build();
    }

    private List<TourImageResponse> mapToImageResponses(List<TourImage> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }

        return images.stream()
                .map(img -> TourImageResponse.builder()
                        .imageID(img.getImageID())
                        .imageURL(img.getImageURL())
                        .isMainImage(img.getIsMainImage())
                        .build())
                .collect(Collectors.toList());
    }

    private List<TourMediaResponse> mapToMediaResponses(List<TourMedia> mediaList) {
        if (mediaList == null || mediaList.isEmpty()) {
            return new ArrayList<>();
        }

        return mediaList.stream()
                .map(media -> TourMediaResponse.builder()
                        .mediaId(media.getMediaId())
                        .mediaUrl(media.getMediaUrl())
                        .thumbnailUrl(media.getThumbnailUrl())
                        .title(media.getTitle())
                        .description(media.getDescription())
                        .duration(media.getDuration())
                        .fileSize(media.getFileSize())
                        .isPrimary(media.getIsPrimary())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ItineraryDayResponse> mapToItineraryResponses(List<ItineraryDay> itineraryDays) {
        if (itineraryDays == null || itineraryDays.isEmpty()) {
            return new ArrayList<>();
        }

        // FIX: Đảm bảo sort theo dayNumber
        return itineraryDays.stream()
                .sorted((a, b) -> a.getDayNumber().compareTo(b.getDayNumber()))
                .map(day -> ItineraryDayResponse.builder()
                        .itineraryDayID(day.getItineraryDayID())
                        .dayNumber(day.getDayNumber())
                        .title(day.getTitle())
                        .meals(day.getMeals())
                        .details(day.getDetails())
                        .build())
                .collect(Collectors.toList());
    }

    private LocationResponse mapToLocationResponse(Location location) {
        return LocationResponse.builder()
                .locationID(location.getLocationID())
                .name(location.getName())
                .slug(location.getSlug())
                .region(Region.valueOf(location.getRegion().name()))
                .airportCode(location.getAirportCode())
                .airportName(location.getAirportName())
                .build();
    }
}