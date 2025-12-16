package com.tourism.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDayResponse {
    private Integer itineraryDayID;
    private Integer dayNumber;
    private String title;
    private String meals;
    private String details;
}