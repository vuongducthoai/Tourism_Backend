package com.tourism.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_media")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TourMedia extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Integer mediaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @Column(name = "thumbnailUrl", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;
}
