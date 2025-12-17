package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartureListResponse {

    private List<DepartureSummaryResponse> departures;

    private Integer currentPage;

    private Integer totalPages;

    private Long totalItems;

    private Integer pageSize;

    private Boolean hasNext;

    private Boolean hasPrevious;
}