package com.tourism.backend.entity;
import com.tourism.backend.enums.PaymentMethod;
import com.tourism.backend.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentID;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    private BigDecimal amount;
    private String transactionId;
    private String bankTransactionNo;
    private String bankCode; // Mã ngân hàng người chuyển khoản ( VCB, TCB, MB)
    private LocalDateTime paymentDate;

    @Column(name = "account_name", nullable = true)
    private String accountName;

    @Column(name = "account_number", nullable = true)
    private String accountNumber;

    @Column(name = "bank_name", nullable = true)
    private String bank;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "time_limit")
    private LocalDateTime timeLimit;
 
    @Column(name = "payment_description", length = 500)
    private String paymentDescription; //Nội dung chuyển khoản

    @Column(name = "transaction_datetime")
    private LocalDateTime transactionDatetime; // Thời gian giao dịch thực tế từ ngân hàng

    // OneToOne with Booking
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;
}