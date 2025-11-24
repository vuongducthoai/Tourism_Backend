package com.tourism.backend.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class TourCreateDTO {
    private String tourCode;
    private String tourName;
    private String duration;
    private String transportation;
    private Integer startLocationId;
    private Integer endLocationId;
    private String attractions;
    private String meals;
    private String hotel;
    private String suitableCustomer;
    private String idealTime;
    private String tripTransportation;
    private List<MultipartFile> images;
}