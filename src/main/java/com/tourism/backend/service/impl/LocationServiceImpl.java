package com.tourism.backend.service.impl;

import com.tourism.backend.convert.LocationConverter;
import com.tourism.backend.dto.request.LocationRequest;
import com.tourism.backend.dto.response.LocationResponse;
import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.LocationResponseDTO;
import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceInUseException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.LocationRepository;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Location> getLocationsByRegion(Region region) {
        return locationRepository.findByRegion(region);
    }

    @Override
    public List<DestinationResponseDTO> getUniqueEndDestinationsByRegion(Region region) {
        List<Location> uniqueEndLocations =
                locationRepository.findUniqueEndLocations(region);
        return locationConverter.toDestinationResponseDTOList(uniqueEndLocations);
    }

    @Override
    public List<LocationResponseDTO> getAllUniqueEndLocations() {
        List<Location> uniqueEndLocations =
                locationRepository.findUniqueEndLocations();
        return locationConverter.toLocationResponseDTOList(uniqueEndLocations);
    }

    @Override
    public List<LocationResponseDTO> getAllUniqueStartLocations() {
        List<Location> uniqueEndLocations =
                locationRepository.findUniqueStartLocations();
        return locationConverter.toLocationResponseDTOList(uniqueEndLocations);
    }

    @Override
    public Page<LocationResponse> getAllLocations(Pageable pageable, String search, String regionStr) {
        Region region = null;
        if (regionStr != null && !regionStr.isBlank()) {
            try {
                region = Region.valueOf(regionStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid region: {}", regionStr);
            }
        }

        Page<Location> locations = locationRepository.searchLocations(search, region, pageable);
        return locations.map(this::mapToResponse);
    }

    @Override
    public LocationResponse getLocationById(Integer id) {
        Location location = findLocationById(id);
        return mapToResponse(location);
    }

    @Override
    @Transactional
    public LocationResponse createLocation(LocationRequest request) {
        log.info("Creating new location: {}", request.getName());

        // Validate unique name
        if (locationRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Địa điểm '" + request.getName() + "' đã tồn tại");
        }

        // Validate unique slug
        if (locationRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Slug '" + request.getSlug() + "' đã được sử dụng");
        }

        // Auto-fill airport info if not provided
        if ((request.getAirportCode() == null || request.getAirportName() == null)) {
            com.tourism.backend.util.VietnamAirportUtils.getAirportInfo(request.getName()).ifPresent(airportInfo -> {
                if (request.getAirportCode() == null) {
                    request.setAirportCode(airportInfo.getCode());
                }
                if (request.getAirportName() == null) {
                    request.setAirportName(airportInfo.getName());
                }
                log.info("Auto-filled airport info for {}: {} - {}",
                        request.getName(), airportInfo.getCode(), airportInfo.getName());
            });
        }

        Location location = mapToEntity(request);
        Location saved = locationRepository.save(location);

        log.info("Created location with ID: {}", saved.getLocationID());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(Integer id, LocationRequest request) {
        log.info("Updating location ID: {}", id);

        Location location = findLocationById(id);

        // Validate unique name (exclude current location)
        if (!location.getName().equals(request.getName()) &&
                locationRepository.existsByNameAndNotId(request.getName(), id)) {
            throw new DuplicateResourceException("Địa điểm '" + request.getName() + "' đã tồn tại");
        }

        // Validate unique slug (exclude current location)
        if (!location.getSlug().equals(request.getSlug()) &&
                locationRepository.existsBySlugAndNotId(request.getSlug(), id)) {
            throw new DuplicateResourceException("Slug '" + request.getSlug() + "' đã được sử dụng");
        }

        // Auto-fill airport if changed name and fields are empty
        if (!location.getName().equals(request.getName()) &&
                (request.getAirportCode() == null || request.getAirportName() == null)) {
            com.tourism.backend.util.VietnamAirportUtils.getAirportInfo(request.getName()).ifPresent(airportInfo -> {
                if (request.getAirportCode() == null) {
                    request.setAirportCode(airportInfo.getCode());
                }
                if (request.getAirportName() == null) {
                    request.setAirportName(airportInfo.getName());
                }
                log.info("Auto-updated airport info: {} - {}",
                        airportInfo.getCode(), airportInfo.getName());
            });
        }

        // Update fields
        location.setName(request.getName());
        location.setSlug(request.getSlug());
        location.setRegion(request.getRegion());
        location.setDescription(request.getDescription());
        location.setAirportCode(request.getAirportCode());
        location.setAirportName(request.getAirportName());

        // Update image if provided
        if (request.getImage() != null) {
            location.setImage(request.getImage());
        }

        Location updated = locationRepository.save(location);
        log.info("Updated location ID: {}", id);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteLocation(Integer id) {
        log.info("Deleting location ID: {}", id);

        Location location = findLocationById(id);

        Long startPointCount = locationRepository.countToursAsStartPoint(id);
        Long endPointCount = locationRepository.countToursAsEndPoint(id);

        if (startPointCount > 0 || endPointCount > 0) {
            throw new ResourceInUseException(
                    String.format("Không thể xóa địa điểm '%s'. Đang có %d tour sử dụng làm điểm xuất phát và %d tour sử dụng làm điểm đến",
                            location.getName(), startPointCount, endPointCount)
            );
        }
        location.setStatus(false);
        locationRepository.save(location);
        log.info("Soft deleted (deactivated) location ID: {}", id);
    }

    @Override
    public String uploadImage(Integer id, MultipartFile file) throws IOException {
        log.info("Uploading image for location ID: {}", id);

        Location location = findLocationById(id);

        // Delete old image if exists
        if (location.getImage() != null && !location.getImage().isEmpty()) {
            try {
                cloudinaryService.deleteImage(location.getImage());
            } catch (Exception e) {
                log.warn("Failed to delete old image: {}", e.getMessage());
            }
        }

        // Upload new image
        String imageUrl = cloudinaryService.uploadImage(file, "locations");
        location.setImage(imageUrl);
        locationRepository.save(location);

        log.info("Uploaded image for location ID: {}", id);
        return imageUrl;
    }

    private Location findLocationById(Integer id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa điểm với ID: " + id));
    }

    private Location mapToEntity(LocationRequest request) {
        Location location = new Location();
        location.setName(request.getName());
        location.setSlug(request.getSlug());
        location.setRegion(request.getRegion());
        location.setDescription(request.getDescription());
        location.setAirportCode(request.getAirportCode());
        location.setAirportName(request.getAirportName());
        location.setImage(request.getImage());
        return location;
    }


    private LocationResponse mapToResponse(Location location) {
        Long startPointCount = locationRepository.countToursAsStartPoint(location.getLocationID());
        Long endPointCount = locationRepository.countToursAsEndPoint(location.getLocationID());

        return LocationResponse.builder()
                .locationID(location.getLocationID())
                .name(location.getName())
                .slug(location.getSlug())
                .image(location.getImage())
                .region(location.getRegion())
                .description(location.getDescription())
                .airportCode(location.getAirportCode())
                .airportName(location.getAirportName())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .toursAsStartPoint(startPointCount)
                .toursAsEndPoint(endPointCount)
                .build();
    }
}