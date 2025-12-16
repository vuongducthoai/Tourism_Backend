package com.tourism.backend.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTourRequest {
    @Valid
    private TourGeneralInfoRequest generalInfo;

    private List<TourImageRequest> images;
    private List<TourMediaRequest> mediaList;

    @Valid
    private List<ItineraryDayRequest> itineraryDays;
}
