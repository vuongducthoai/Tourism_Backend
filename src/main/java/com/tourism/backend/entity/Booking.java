package com.tourism.backend.entity;

import com.tourism.backend.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Booking extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingID;

    @Column(name = "booking_code", unique = true, nullable = false, length = 20)
    private String bookingCode;

    //Function initial automatic bookingCode when create new object
    @PrePersist
    public void generateBookingCode(){
        if(this.bookingCode == null){
            this.bookingCode = "BK" + UUID.randomUUID().toString().substring(0, 8);
        }
    }

    @NotNull
    private LocalDateTime bookingDate = LocalDateTime.now();

    @NotBlank(message = "Contact email is required")
    @Email
    private String contactEmail;

    @NotBlank(message = "Contact name is required")
    private String contactFullName;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotBlank(message = "Address is required")
    private String contactAddress;

    @Column(columnDefinition = "TEXT")
    private String customerNote;

    @Min(1)
    private Integer totalPassengers; // tổng số khách booking

    @NotNull
    @Min(0)
    private BigDecimal subtotalPrice;

    private BigDecimal surcharge; //

    //So tien duoc giam gia tu Coupon
    @Column(name = "coupon_discount", nullable = false)
    private BigDecimal couponDiscount = BigDecimal.ZERO;

    //So tien duoc giam tu Coin de thanh toan
    @Column(name = "paid_by_coin", nullable = false)
    private BigDecimal paidByCoin = BigDecimal.ZERO;

    // Tong tien phai tra = Subtotal + Surcharge - Discount - PaidByCoin
    @NotNull
    @Min(0)
    private BigDecimal totalPrice;

    @Column(columnDefinition = "TEXT")
    private String cancelReason; //Ly do huy don

    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    // --- Relationships ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id", nullable = false)
    private TourDeparture tourDeparture;

    @Column(name = "applied_coupon_codes", columnDefinition = "TEXT")
    private String appliedCouponCodes;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingPassenger> passengers;


    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RefundInformation refundInformation;
}