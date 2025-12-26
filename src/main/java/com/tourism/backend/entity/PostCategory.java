package com.tourism.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @Column(nullable = false, unique = true, length = 100)
    private String categoryName;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String icon; // Icon name or emoji

    @Column(length = 50)
    private String color; // Màu đại diện category

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ForumPost> posts = new ArrayList<>();
}