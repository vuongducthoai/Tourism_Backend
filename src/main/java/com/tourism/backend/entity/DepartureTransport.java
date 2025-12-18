package com.tourism.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tourism.backend.enums.TransportType;
import com.tourism.backend.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "departure_transports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepartureTransport extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transportID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "vehicle_type")
    private VehicleType vehicleTyle;

    @Column(name = "transport_code")
    private String transportCode;

    @Column(name = "vehicle_name")
    private String vehicleName;

    @Column(name = "start_point")
    private String startPoint; // SGN

    @Column(name = "end_point")
    private String endPoint;

    @Column(name = "depart_time")
    private LocalDateTime departTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id", nullable = false)
    @JsonIgnore
    private TourDeparture tourDeparture;
}
