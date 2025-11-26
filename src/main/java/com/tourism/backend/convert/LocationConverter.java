package com.tourism.backend.convert;

import com.tourism.backend.dto.responseDTO.DestinationResponseDTO;
import com.tourism.backend.entity.Location;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocationConverter {

    // Inject ModelMapper đã được cấu hình trong ModelMapperConfig
    private final ModelMapper modelMapper;
    public DestinationResponseDTO toDestinationResponseDTO(Location location) {
        DestinationResponseDTO dto = modelMapper.map(location, DestinationResponseDTO.class);

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
}