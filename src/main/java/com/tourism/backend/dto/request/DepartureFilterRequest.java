package com.tourism.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureFilterRequest {
    private Integer tourId;

    private List<Integer> tourIds;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean status;

    private Integer minAvailableSlots;

    private Integer maxAvailableSlots;

    private Integer policyTemplateId;

    private Integer couponId;

    private Integer page;

    private Integer size;

    private String sortBy;

    private String sortDirection;
}
