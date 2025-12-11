package com.tourism.backend.dto.payos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentData {

    @JsonProperty("orderCode")
    private Long orderCode;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("description")
    private String description;

    @JsonProperty("items")
    private List<ItemData> items;

    @JsonProperty("returnUrl")
    private String returnUrl;

    @JsonProperty("cancelUrl")
    private String cancelUrl;

    @JsonProperty("buyerName")
    private String buyerName;

    @JsonProperty("buyerEmail")
    private String buyerEmail;

    @JsonProperty("buyerPhone")
    private String buyerPhone;

    @JsonProperty("buyerAddress")
    private String buyerAddress;

    @JsonProperty("expiredAt")
    private Long expiredAt;
}
