package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourSimpleResponse {
    private Integer tourID;
    private String tourCode;
    private String tourName;
}