package com.tourism.backend.service;

import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import java.util.List;

public interface LocationService {
    List<Location> getLocationsByRegion(Region region);
}