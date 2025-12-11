package com.tourism.backend.service;

import com.tourism.backend.dto.responseDTO.SepayTransactionResponse;
import com.tourism.backend.dto.responseDTO.TransactionVerificationDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SepayService {

    /**
     * Lấy danh sách giao dịch gần đây (30 ngày)
     */
    List<SepayTransactionResponse.Transaction> getRecentTransactions();

    /**
     * Kiểm tra xem có giao dịch chuyển khoản khớp với thông tin booking không
     */
    TransactionVerificationDTO verifyRefundTransaction(
            String bookingCode,
            BigDecimal amount,
            String accountNumber,
            String accountName,
            String bankCode
    );

    /**
     * Tạo nội dung chuyển khoản cho VietQR
     */
    String generateTransferContent(String bookingCode);
}