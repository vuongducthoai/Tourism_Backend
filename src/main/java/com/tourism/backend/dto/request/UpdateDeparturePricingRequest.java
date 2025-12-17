package com.tourism.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeparturePricingRequest {
    @Valid
    @NotEmpty(message = "Danh sách giá không được rỗng")
    private List<DeparturePricingRequest> pricings;
}
