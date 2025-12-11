package com.tourism.backend.dto.requestDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SepayTransferRequestDTO {

    @JsonProperty("account_number")
    private String accountNumber; // Số tài khoản người nhận

    @JsonProperty("account_name")
    private String accountName; // Tên tài khoản người nhận

    @JsonProperty("amount")
    private Long amount; // Số tiền (VNĐ, không có phần thập phân)

    @JsonProperty("description")
    private String description; // Nội dung chuyển khoản

    @JsonProperty("bank_code")
    private String bankCode; // Mã ngân hàng (VD: VCB, TCB, MB...)
}
