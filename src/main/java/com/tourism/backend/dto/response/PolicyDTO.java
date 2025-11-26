package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicyDTO {
    private String childPricingNotes;
    private String paymentConditions;
    private String registrationConditions;
    private String regularDayCancellationRules;
    private String holidayCancellationRules;
    private String forceMajeureRules;
    private String packingList;
    private BrachContactDTO brachContactDTO;
}
