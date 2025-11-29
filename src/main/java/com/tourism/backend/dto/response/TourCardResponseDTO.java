package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TourCardResponseDTO {
    private Integer tourId;
    private String tourName;
    private String tourCode;
    private String duration;
    private String startLocation;
    private String image;
    private BigDecimal price; // salePrice
    private BigDecimal originalPrice;
}