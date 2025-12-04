package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refund_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefundInformation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer refundID;

    @NotBlank(message = "Account name is required")
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @NotBlank(message = "Bank name is required")
    @Column(name = "bank_name", nullable = false)
    private String bank;

    // --- Relationship: OneToOne với Booking ---
    // Đây là phía sở hữu (owning side), sử dụng @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true, nullable = false)
    @NotNull(message = "Booking must be associated with refund information")
    private Booking booking;
}