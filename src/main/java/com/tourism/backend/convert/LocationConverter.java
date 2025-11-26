package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.dto.responseDTO.LocationResponseDTO;
import com.tourism.backend.entity.Location;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocationConverter {

    private final ModelMapper modelMapper;

    public DestinationResponseDTO toDestinationResponseDTO(Location location) {
        // Ánh xạ các trường có tên trùng (locationID, name, region)
        DestinationResponseDTO dto = modelMapper.map(location, DestinationResponseDTO.class);

        // Ánh xạ thủ công trường image -> imageUrl
        if (location.getImage() != null) {
            dto.setImageUrl(location.getImage());
        }

        return dto;
    }

    public List<DestinationResponseDTO> toDestinationResponseDTOList(List<Location> locations) {
        return locations.stream()
                .map(this::toDestinationResponseDTO)
                .collect(Collectors.toList());
    }
    public LocationResponseDTO toLocationResponseDTO(Location location) {
        LocationResponseDTO dto = modelMapper.map(location, LocationResponseDTO.class);

        // Cần ánh xạ thủ công cho trường 'image'
        if (location.getImage() != null) {
            dto.setImageUrl(location.getImage());
        }
        return dto;
    }
    public List<LocationResponseDTO> toLocationResponseDTOList(List<Location> locations) {
        return locations.stream()
                .map(this::toLocationResponseDTO)
                .collect(Collectors.toList());
    }
}