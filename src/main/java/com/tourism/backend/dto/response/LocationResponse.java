package com.tourism.backend.dto.response;

import com.tourism.backend.enums.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {

    private Integer locationID;
    private String name;
    private String slug;
    private String image;
    private Region region;
    private String description;
    private String airportCode;
    private String airportName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long toursAsStartPoint;
    private Long toursAsEndPoint;
}