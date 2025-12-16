package com.tourism.backend.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailResponse {
    private Integer tourID;
    private String tourCode;
    private String tourName;
    private String duration;
    private String transportation;

    private Integer startLocationId;
    private String startLocationName;
    private Integer endLocationId;
    private String endLocationName;

    private String attractions;
    private String meals;
    private String idealTime;
    private String tripTransportation;
    private String suitableCustomer;
    private String hotel;
    private Boolean status;

    private List<TourImageResponse> images;
    private List<TourMediaResponse> mediaList;
    private List<ItineraryDayResponse> itineraryDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}