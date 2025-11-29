package com.tourism.backend.dto.responseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TourSpecialResponseDTO {
    private Integer departureID;
    private Integer tourID;
    private String tourName;
    private String tourCode;
    private String startLocationName;
    private String duration;
    private LocalDate departureDate;
    private Integer availableSlots;
    private BigDecimal salePrice;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;
    private String image;
}
