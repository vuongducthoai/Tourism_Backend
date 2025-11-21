package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "tour_departures")
@Data
@EqualsAndHashCode(callSuper = true)
public class TourDeparture extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departureID;

    @Column(name = "departure_date", nullable = false)
    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate departureDate;

    @NotNull(message = "Available slots are required")
    @Min(value = 1, message = "Available slots must be at least 1")
    private Integer availableSlots;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Tour guide info is required")
    private String tourGuideInfo;

    @Column(name = "policy_template_id")
    @NotNull(message = "Policy template ID is required")
    private Integer policyTemplateID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_code", nullable = false)
    private Tour tour;
}