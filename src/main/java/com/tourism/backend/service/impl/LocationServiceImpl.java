package com.tourism.backend.service.impl;

import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import com.tourism.backend.repository.LocationRepository;
import com.tourism.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public List<Location> getLocationsByRegion(Region region) {
        return locationRepository.findByRegion(region);
    }
}