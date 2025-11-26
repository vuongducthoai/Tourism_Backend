package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDTO {
    private Integer id;
    private String name;
    private String airportCode;
}
