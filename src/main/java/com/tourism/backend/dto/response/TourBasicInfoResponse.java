package com.tourism.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourBasicInfoResponse {
    private Integer tourId;

    private String tourCode;

    private String tourName;

    private String duration;

    private String transportation;

    private String startLocationName;

    private String endLocationName;
}
