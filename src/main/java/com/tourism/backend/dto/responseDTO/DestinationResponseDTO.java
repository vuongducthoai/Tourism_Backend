package com.tourism.backend.dto.responseDTO;

import com.tourism.backend.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO này được thiết kế để khớp với DestinationMockData[] ở Frontend
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DestinationResponseDTO {
    private String name;
    private String imageUrl;
    private Region region;
}