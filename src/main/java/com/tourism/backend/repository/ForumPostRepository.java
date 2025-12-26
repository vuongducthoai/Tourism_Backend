package com.tourism.backend.repository;

import com.tourism.backend.entity.ForumPost;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.ContentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ForumPostRepository extends JpaRepository<ForumPost, Integer>, JpaSpecificationExecutor<ForumPost> {
    Page<ForumPost> findByUserUserIDAndStatus(
            Integer userId,
            ContentStatus status,
            Pageable pageable
    );

    /**
     * Tìm bài trending (nhiều tương tác trong 7 ngày)
     * Sắp xếp theo công thức: viewCount * 0.3 + likeCount * 2 + commentCount * 3
     */
    @Query("""
        SELECT p FROM ForumPost p 
        WHERE p.status = 'PUBLISHED' 
        AND p.publishedAt >= :weekAgo
        ORDER BY (p.viewCount * 0.3 + p.likeCount * 2 + p.commentCount * 3) DESC
        """)
    Page<ForumPost> findTrendingPosts(
            @Param("weekAgo") LocalDateTime weekAgo,
            Pageable pageable
    );

    /**
     * Tìm bài pinned
     */
    Page<ForumPost> findByIsPinnedTrueAndStatusOrderByCreatedAtDesc(
            ContentStatus status,
            Pageable pageable
    );

    /**
     * Tìm bài featured
     */
    Page<ForumPost> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(
            ContentStatus status,
            Pageable pageable
    );

    long countByUserAndStatus(User user, ContentStatus status);
}
