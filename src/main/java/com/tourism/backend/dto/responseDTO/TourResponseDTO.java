package com.tourism.backend.dto.responseDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TourResponseDTO {
    private Integer tourID;
    private String tourCode;
    private String tourName;
    private String startPointName;
    private String transportation;
    private String duration;
    private List<TourDepartureDateResponseDTO> departureDates;
    private Long money;
    private String image;
    private Boolean isFavorite;
}
