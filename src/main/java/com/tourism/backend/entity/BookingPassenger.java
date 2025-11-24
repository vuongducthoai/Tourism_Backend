package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "booking_passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingPassenger extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingPassengerID;

    @NotBlank(message = "Passenger name is required")
    private String fullName;

    @NotBlank
    private String gender;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    @Pattern(regexp = "^(ADULT|CHILD|INFANT)$")
    private String passengerType;

    //Giá gốc niêm yết (Ví dụ: 5.000.000)
    private BigDecimal originalPrice;

    // Giá thực bán tại thời điểm đặt (Ví dụ: 4.000.000)
    @NotNull
    private BigDecimal basePrice;

    private Boolean requiresSingleRoom = false;

    private BigDecimal singleRoomSurcharge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}