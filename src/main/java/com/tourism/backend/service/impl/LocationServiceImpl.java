package com.tourism.backend.service.impl;

import com.tourism.backend.convert.LocationConverter;
import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.LocationResponseDTO;
import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import com.tourism.backend.repository.LocationRepository;
import com.tourism.backend.service.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;
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

}