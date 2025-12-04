package com.tourism.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"images", "itineraryDays", "departures", "coupons"})
public class Tour extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tourID;

    @Column(name = "tour_code", length = 50, unique = true, nullable = false)
    @NotBlank(message = "Tour code is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Tour code must contain only uppercase letters, numbers, and hyphens")
    private String tourCode;

    @PrePersist
    @PreUpdate
    public void normalizeTourCode(){
        if(this.tourCode != null){
            this.tourCode = this.tourCode.trim().toUpperCase();
        }
    }

    @Column(name = "tour_name", nullable = false)
    @NotBlank(message = "Tour name is required")
    @Size(min = 10, max = 255, message = "Tour name must be between 10 and 255 characters")
    private String tourName;

    @NotBlank(message = "Duration is required")
    private String duration; // e.g., "3 Days 2 Nights"

    @NotBlank(message = "Transportation info is required")
    private String transportation;

    @NotNull(message = "Điểm khởi hành không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @NotNull(message = "Điểm đến không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_location_id", nullable = false)
    private Location endLocation;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Attractions description is required")
    private String attractions;

    @Column(columnDefinition = "TEXT")
    private String meals;

    @Column(name = "ideal_time")
    private String idealTime;

    @Column(name = "status")
    private Boolean status = true;

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
    @JsonIgnore
    private List<TourDeparture> departures;
}