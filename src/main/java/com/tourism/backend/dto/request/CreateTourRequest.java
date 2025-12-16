package com.tourism.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTourRequest {
    @Valid
    @NotNull(message = "Thông tin chung không được để trống")
    private TourGeneralInfoRequest generalInfo;

    private List<TourImageRequest> images;
    private List<TourMediaRequest> mediaList;

    @Valid
    private List<ItineraryDayRequest> itineraryDays;
}
