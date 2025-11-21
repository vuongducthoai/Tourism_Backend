package com.tourism.backend.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "itinerary_days")
@Data
@EqualsAndHashCode(callSuper = true)
public class ItineraryDay extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itineraryDayID;

    @NotNull(message = "Day number is required")
    @Min(value = 1, message = "Day number must start from 1")
    private Integer dayNumber;

    @NotBlank(message = "Itinerary title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Meal info is required")
    private String meals;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Itinerary details are required")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_code", nullable = false)
    private Tour tour;
}