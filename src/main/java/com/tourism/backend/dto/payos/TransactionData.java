package com.tourism.backend.dto.payos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {
    @SerializedName("reference")
    private String reference;

    @SerializedName("amount")
    private Integer amount;

    @SerializedName("accountNumber")
    private String accountNumber;

    @SerializedName("description")
    private String description;

    @SerializedName("transactionDateTime")
    private String transactionDateTime;

    @SerializedName("virtualAccountName")
    private String virtualAccountName;

    @SerializedName("virtualAccountNumber")
    private String virtualAccountNumber;

    @SerializedName(value = "counterAccountBankId", alternate = {"counter_account_bank_id"})
    private String counterAccountBankId;

    @SerializedName(value = "counterAccountBankName", alternate = {"counter_account_bank_name"})
    private String counterAccountBankName;

    @SerializedName(value = "counterAccountName", alternate = {"counter_account_name"})
    private String counterAccountName;

    @SerializedName(value = "counterAccountNumber", alternate = {"counter_account_number"})
    private String counterAccountNumber;
}
