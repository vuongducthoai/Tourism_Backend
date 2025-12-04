package com.tourism.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageReviewID; // Khóa chính

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Image URL is required")
    private String image; // Đường dẫn (URL) của hình ảnh

    // --- Relationship: ManyToOne với Review ---
    // Đây là phía sở hữu (owning side) của quan hệ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false) // Khóa ngoại trỏ đến Review
    private Review review;
}