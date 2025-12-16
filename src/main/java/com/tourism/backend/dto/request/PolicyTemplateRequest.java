package com.tourism.backend.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTemplateRequest {
    @NotBlank(message = "Tên template không được để trống")
    private String templateName;

    @NotNull(message = "Contact ID không được để trống")
    private Integer contactId;

    private String tourPriceIncludes;
    private String tourPriceExcludes;
    private String childPricingNotes;
    private String paymentConditions;
    private String registrationConditions;
    private String regularDayCancellationRules;
    private String holidayCancellationRules;
    private String forceMajeureRules;
    private String packingList;
}
