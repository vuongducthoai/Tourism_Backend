package com.tourism.backend.entity;

import com.tourism.backend.enums.ContentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_comments", indexes = {
        // SỬA: Thêm prefix 'post_comments_'
        @Index(name = "idx_post_comments_post_id", columnList = "post_id"),
        @Index(name = "idx_post_comments_parent_id", columnList = "parent_comment_id"),
        @Index(name = "idx_post_comments_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private PostComment parentComment;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Boolean isEdited = false;

    @Column
    private LocalDateTime editedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.PUBLISHED;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();
}