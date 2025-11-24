package com.tourism.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tourism.backend.enums.Region;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer locationID;

    @Column(nullable = false, unique = true)
    private String name; //Hà Nội, Đà Nẵng, TP.HCM

    @Column(nullable = false, unique = true)
    private String slug; //ha-noi, da-nang (Dùng cho URL)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @OneToMany(mappedBy = "startLocation")
    @JsonIgnore
    private List<Tour> startPoint;

    @OneToMany(mappedBy = "endLocation")
    @JsonIgnore
    private List<Tour> endPoint;
}