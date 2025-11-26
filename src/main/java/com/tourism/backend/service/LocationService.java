package com.tourism.backend.service;

import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.LocationResponseDTO;
import com.tourism.backend.entity.Location;
import com.tourism.backend.enums.Region;
import java.util.List;

public interface LocationService {
    List<Location> getLocationsByRegion(Region region);
    // ✨ PHƯƠNG THỨC MỚI: Lấy điểm đến cuối duy nhất và chuyển sang DTO
    List<DestinationResponseDTO> getUniqueEndDestinationsByRegion(Region region);
    List<LocationResponseDTO> getAllUniqueEndLocations();
    List<LocationResponseDTO> getAllUniqueStartLocations();

}