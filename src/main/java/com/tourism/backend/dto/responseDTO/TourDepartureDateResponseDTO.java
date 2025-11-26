package com.tourism.backend.dto.responseDTO;

import lombok.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TourDepartureDateResponseDTO {
    private Integer departureID;
    private LocalDate departureDate; // Chỉ lấy ngày (Date)
}