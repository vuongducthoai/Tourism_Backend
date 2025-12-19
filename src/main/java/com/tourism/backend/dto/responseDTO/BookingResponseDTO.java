package com.tourism.backend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {
    private Integer bookingID;
    private String bookingCode;
    private LocalDateTime bookingDate = LocalDateTime.now();
    private String contactEmail;
    private String contactFullName;
    private String contactPhone;
    private String contactAddress;
    private String customerNote;
    private Integer totalPassengers; // tổng số khách booking
    private BigDecimal surcharge; //
    private BigDecimal subtotalPrice;
    private BigDecimal couponDiscount ;
    private BigDecimal paidByCoin;
    private BigDecimal totalPrice;
    private String cancelReason; //Ly do huy don
    private String bookingStatus;
    private Integer departureID;
    private LocalDateTime departureDate;
    private Integer tourID;
    private String tourCode;
    private String tourName;
    private String image;
    private Integer paymentID;
    private BigDecimal amount;
    private LocalDateTime timeLimit;
    private List<BookingPassengerResponseDTO> passengers;
    private String bank;
    private String accountNumber;
    private String accountName;
    private String refundBank;
    private String refundAccountNumber;
    private String refundAccountName;
}
