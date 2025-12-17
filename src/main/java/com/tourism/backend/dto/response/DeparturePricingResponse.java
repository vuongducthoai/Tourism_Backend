package com.tourism.backend.dto.response;

import com.tourism.backend.enums.PassengerType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeparturePricingResponse {

    private Integer pricingID;

    private PassengerType passengerType;

    private String passengerTypeDisplay;

    private String ageDescription;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal originalPrice;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal salePrice;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal discountAmount;

    private Integer discountPercent;
}
