package com.tourism.backend.repository;

import com.tourism.backend.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    long countByIsActiveTrue();
    @Query("""
        SELECT t AS tag, 
               COUNT(pt) AS usageCount 
        FROM Tag t 
        LEFT JOIN t.postTags pt 
        JOIN pt.post p 
        WHERE t.isActive = true 
          AND p.status = 'PUBLISHED'
        GROUP BY t 
        ORDER BY t.tagName ASC
        """)
    List<Object[]> findAllActiveWithUsageCount(Pageable pageable);

    @Query("""
        SELECT t AS tag, 
               COUNT(pt) AS usageCount 
        FROM Tag t 
        LEFT JOIN t.postTags pt 
        JOIN pt.post p 
        WHERE t.isActive = true 
          AND p.status = 'PUBLISHED'
        GROUP BY t 
        ORDER BY COUNT(pt) DESC, t.tagName ASC
        """)
    List<Object[]> findPopularTagsWithCount(Pageable pageable);

    Optional<Tag> findByTagNameIgnoreCase(String tagName);
    Optional<Tag> findBySlug(String slug);
}