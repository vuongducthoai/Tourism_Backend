package com.tourism.backend.dto.requestDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchToursRequestDTO {
    private String searchNameTour;
    private Integer startPrice;
    private Integer endPrice;
    private Integer startLocationID;
    private Integer endLocationID;
    private String  transportation;
    private Integer rating;
}
