package com.tourism.backend.service;

import com.tourism.backend.dto.request.LocationRequest;
import com.tourism.backend.dto.response.LocationResponse;
import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.LocationResponseDTO;
import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface LocationService {
    List<Location> getLocationsByRegion(Region region);
    List<DestinationResponseDTO> getUniqueEndDestinationsByRegion(Region region);
    List<LocationResponseDTO> getAllUniqueEndLocations();
    List<LocationResponseDTO> getAllUniqueStartLocations();


    Page<LocationResponse> getAllLocations(Pageable pageable, String search, String region);

    LocationResponse getLocationById(Integer id);

    LocationResponse createLocation(LocationRequest request);

    LocationResponse updateLocation(Integer id, LocationRequest request);

    void deleteLocation(Integer id);

    String uploadImage(Integer id, MultipartFile file) throws IOException;
}