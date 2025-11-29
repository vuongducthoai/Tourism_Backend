package com.tourism.backend.dto.responseDTO;

import com.tourism.backend.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponseDTO {
    private Integer locationID;
    private String name;
    private String imageUrl;
    private String description;
}