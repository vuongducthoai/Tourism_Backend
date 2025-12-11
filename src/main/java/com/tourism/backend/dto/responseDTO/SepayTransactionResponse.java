package com.tourism.backend.dto.responseDTO;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SepayTransactionResponse {
    private int status;

    private Object error;

    private Messages messages;

    private List<Transaction> transactions;

    @Data
    public static class Messages {
        private boolean success;
    }

    @Data
    public static class Transaction {

        private String id;

        @SerializedName("transaction_date")
        private String transactionDate;

        @SerializedName("account_number")
        private String accountNumber;

        @SerializedName("amount_in")
        private BigDecimal amountIn;

        @SerializedName("amount_out")
        private BigDecimal amountOut;

        private BigDecimal accumulated;

        @SerializedName("transaction_content")
        private String transactionContent;

        @SerializedName("reference_number")
        private String referenceNumber;

        @SerializedName("sub_account")
        private String subAccount;

        @SerializedName("bank_brand_name")
        private String bankBrandName;

        @SerializedName("bank_account_id")
        private String bankAccountId;

        private String code;
    }
}
