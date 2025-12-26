package com.tourism.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_views", indexes = {
        // SỬA: Thêm prefix 'post_views_' để tránh trùng
        @Index(name = "idx_post_views_post_id", columnList = "post_id"),
        @Index(name = "idx_post_views_viewed_at", columnList = "viewed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer viewID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 100)
    private String sessionId;

    @Column(nullable = false)
    private LocalDateTime viewedAt = LocalDateTime.now();
}