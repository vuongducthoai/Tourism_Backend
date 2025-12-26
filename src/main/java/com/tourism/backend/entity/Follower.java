package com.tourism.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "followers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"following_id", "follower_user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer followerID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following; // Người được follow

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_user_id", nullable = false)
    private User followerUser; // Người follow

    @Column(nullable = false)
    private LocalDateTime followedAt = LocalDateTime.now();
}