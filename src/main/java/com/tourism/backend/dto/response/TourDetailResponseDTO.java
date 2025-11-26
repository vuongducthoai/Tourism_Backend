package com.tourism.backend.dto.response;

import java.util.List;

public class TourDetailResponseDTO {
    private Integer tourId;
    private String tourCode;
    private String tourName;
    private String duration;
    private String transportation;
    private String attractions;
    private String meals;
    private String suitableCustomer;
    private String tripTransportation;
    private String idealTime;

    private List<String> images;
    private DeparturePricingDTO depaturePricingDTO;
    private List<ItineraryDTO> itinerary;
    private List<DepartureDTO> departures;
    private PolicyDTO policy;
}
