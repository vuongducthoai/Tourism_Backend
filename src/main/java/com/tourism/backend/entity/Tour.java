package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Tour extends BaseEntity{
    @Id
    @Column(name = "tour_code", length = 50)
    @NotBlank(message = "Tour code is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Tour code must contain only uppercase letters, numbers, and hyphens")
    private String tourCode;

    @Column(name = "tour_name", nullable = false)
    @NotBlank(message = "Tour name is required")
    @Size(min = 10, max = 255, message = "Tour name must be between 10 and 255 characters")
    private String tourName;

    @NotBlank(message = "Duration is required")
    private String duration; // e.g., "3 Days 2 Nights"

    @NotBlank(message = "Transportation info is required")
    private String transportation;

    @Column(name = "start_point")
    @NotBlank(message = "Start point is required")
    private String startPoint;

    @Column(name = "end_point")
    @NotBlank(message = "End point is required")
    private String endPoint;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Attractions description is required")
    private String attractions;

    @Column(columnDefinition = "TEXT")
    private String meals;

    @Column(name = "ideal_time")
    private String idealTime;

    @Column(name = "trip_transportation")
    private String tripTransportation;

    @Column(name = "suitable_customer")
    private String suitableCustomer;

    @Column(name = "hotel")
    private String hotel;

    // --- Relationships ---
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourImage> images;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItineraryDay> itineraryDays;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourDeparture> departures;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<DeparturePricing> pricings;

    @OneToMany(mappedBy = "tour")
    private List<Coupon> coupons;
}