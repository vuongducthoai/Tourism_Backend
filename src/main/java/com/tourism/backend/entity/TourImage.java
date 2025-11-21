package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "tour_images")
@Data
@EqualsAndHashCode(callSuper = true)
public class TourImage extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageID;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Image URL is required")
    private String imageURL;

    @Column(name = "is_main_image")
    @NotNull
    private Boolean isMainImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_code", nullable = false)
    private Tour tour;
}