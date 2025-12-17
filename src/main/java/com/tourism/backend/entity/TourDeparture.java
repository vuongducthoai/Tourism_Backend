package com.tourism.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tour_departures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"tour", "pricings"})
public class TourDeparture extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departureID;

    @Column(name = "departure_date", nullable = false)
    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate departureDate;

    @Column(name = "status")
    private Boolean status = true;

    @NotNull(message = "Available slots are required")
    @Min(value = 1, message = "Available slots must be at least 1")
    private Integer availableSlots;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Tour guide info is required")
    private String tourGuideInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_template_id", nullable = false)
    private PolicyTemplate policyTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    @JsonIgnore
    private Tour tour;

    @OneToMany(mappedBy = "tourDeparture", cascade = CascadeType.ALL)
    private List<DepartureTransport> transports;

    @OneToMany(mappedBy = "tourDeparture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeparturePricing> pricings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToMany(mappedBy = "tourDeparture", fetch = FetchType.LAZY)
    private List<Booking> bookings;
}