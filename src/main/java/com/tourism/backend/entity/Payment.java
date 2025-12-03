package com.tourism.backend.entity;
import com.tourism.backend.enums.PaymentMethod;
import com.tourism.backend.enums.PaymentStatus;
import jakarta.persistence.*;
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
    private String bankCode;
    private LocalDateTime paymentDate;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // OneToOne with Booking
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;
}