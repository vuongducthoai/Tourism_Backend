package com.tourism.backend.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SePayTransferRequest {
    private String account_number;      // Số tài khoản nhận
    private String account_name;        // Tên tài khoản nhận
    private BigDecimal amount;          // Số tiền (VNĐ)
    private String content;             // Nội dung chuyển khoản
    private String bank_code;           // Mã ngân hàng (VCB, TCB, MB...)
}