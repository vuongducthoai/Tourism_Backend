package com.tourism.backend.entity;

import com.tourism.backend.enums.PassengerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "departure_pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "tourDeparture")
public class DeparturePricing extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pricingID;

    @NotNull(message = "Passenger type is required")
    @Enumerated(EnumType.STRING)
    private PassengerType passengerType;

    @Column(name = "age_description")
    @NotBlank(message = "Age description is required")
    private String ageDescription; // e.g., "Age 5-10"

    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal salePrice;  // giá ban neu co ap dung coupon

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal originalPrice; // gia goc

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id", nullable = false)
    private TourDeparture tourDeparture;
}