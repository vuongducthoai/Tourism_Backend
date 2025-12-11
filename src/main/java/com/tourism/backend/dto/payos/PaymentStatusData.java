package com.tourism.backend.dto.payos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusData {

    @JsonProperty("id")
    private String id;

    @JsonProperty("orderCode")
    private Long orderCode;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("amountPaid")
    private Integer amountPaid;

    @JsonProperty("amountRemaining")
    private Integer amountRemaining;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("transactions")
    private java.util.List<TransactionData> transactions;

    @JsonProperty("cancellationReason")
    private String cancellationReason;

    @JsonProperty("canceledAt")
    private String canceledAt;
}
