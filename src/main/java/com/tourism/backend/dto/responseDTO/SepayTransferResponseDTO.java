package com.tourism.backend.dto.responseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
class SepayTransferResponseDTO {

    @JsonProperty("status")
    private Integer status; // 200 = thành công

    @JsonProperty("messages")
    private SepayMessages messages;

    @JsonProperty("data")
    private SepayTransferData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SepayMessages {
        @JsonProperty("success")
        private Boolean success;

        @JsonProperty("message")
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SepayTransferData {
        @JsonProperty("id")
        private String id; // Mã giao dịch

        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("amount")
        private Long amount;

        @JsonProperty("description")
        private String description;

        @JsonProperty("status")
        private String status; // pending, processing, success, failed
    }
}