package com.tourism.backend.service.impl;

import com.google.gson.Gson;
import com.tourism.backend.config.SepayConfig;

import com.tourism.backend.dto.responseDTO.SepayTransactionResponse;
import com.tourism.backend.dto.responseDTO.TransactionVerificationDTO;
import com.tourism.backend.service.SepayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SepayServiceImpl implements SepayService {

    private final SepayConfig sepayConfig;
    private final Gson gson = new Gson();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public List<SepayTransactionResponse.Transaction> getRecentTransactions() {
        try {
            // Lấy giao dịch 30 ngày gần đây
            String url = sepayConfig.getApiUrl() + "/transactions/list?account_number="
                    + sepayConfig.getAccountNumber() + "&limit=100";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + sepayConfig.getToken())
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("SePay API error: {}", response.code());
                    return new ArrayList<>();
                }

                String responseBody = response.body().string();
                log.info("SePay response: {}", responseBody);

                SepayTransactionResponse sepayResponse = gson.fromJson(responseBody, SepayTransactionResponse.class);

                if (sepayResponse != null && sepayResponse.getTransactions() != null) {
                    return sepayResponse.getTransactions();
                }


                return new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("Error fetching SePay transactions", e);
            return new ArrayList<>();
        }
    }

    @Override
    public TransactionVerificationDTO verifyRefundTransaction(
            String bookingCode,
            BigDecimal amount,
            String accountNumber,
            String accountName,
            String bankCode
    ) {
        try {
            log.info("Verifying refund transaction for booking: {}, amount: {}", bookingCode, amount);

            List<SepayTransactionResponse.Transaction> transactions = getRecentTransactions();

            // Tìm giao dịch khớp trong 24h gần đây
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (SepayTransactionResponse.Transaction txn : transactions) {
                // Kiểm tra giao dịch OUT (chuyển tiền đi)
                if (txn.getAmountOut() == null || txn.getAmountOut().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                // Parse transaction date
                LocalDateTime txnDate;
                try {
                    txnDate = LocalDateTime.parse(txn.getTransactionDate(), formatter);
                } catch (Exception e) {
                    log.warn("Cannot parse transaction date: {}", txn.getTransactionDate());
                    continue;
                }

                // Chỉ xét giao dịch trong 24h
                if (txnDate.isBefore(now.minusHours(24))) {
                    continue;
                }

                // Kiểm tra số tiền khớp (cho phép sai lệch 1000 VND)
                BigDecimal difference = txn.getAmountOut().subtract(amount).abs();
                if (difference.compareTo(new BigDecimal("1000")) > 0) {
                    continue;
                }

                // Kiểm tra nội dung chuyển khoản chứa booking code
                String content = txn.getTransactionContent();
                if (content != null && content.toUpperCase().contains(bookingCode.toUpperCase())) {
                    log.info("✅ Found matching transaction: {}", txn);

                    return TransactionVerificationDTO.builder()
                            .bookingCode(bookingCode)
                            .expectedAmount(amount)
                            .expectedAccountNumber(accountNumber)
                            .expectedAccountName(accountName)
                            .expectedBank(bankCode)
                            .verified(true)
                            .transactionReference(txn.getReferenceNumber())
                            .transactionDate(txn.getTransactionDate())
                            .build();
                }
            }

            log.warn("❌ No matching transaction found for booking: {}", bookingCode);

            return TransactionVerificationDTO.builder()
                    .bookingCode(bookingCode)
                    .expectedAmount(amount)
                    .verified(false)
                    .build();

        } catch (Exception e) {
            log.error("Error verifying refund transaction", e);
            return TransactionVerificationDTO.builder()
                    .bookingCode(bookingCode)
                    .verified(false)
                    .build();
        }
    }

    @Override
    public String generateTransferContent(String bookingCode) {
        // Format: HOANTIEN <BOOKING_CODE>
        return "HOANTIEN " + bookingCode.toUpperCase();
    }
}