package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "departure_pricing")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeparturePricing extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pricingID;

    @NotBlank(message = "Passenger type is required")
    @Pattern(regexp = "^(ADULT|CHILD|INFANT)$", message = "Passenger type must be ADULT, CHILD, or INFANT")
    private String passengerType;

    @Column(name = "age_description")
    @NotBlank(message = "Age description is required")
    private String ageDescription; // e.g., "Age 5-10"

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal money;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id", nullable = false)
    private TourDeparture tourDeparture;
}