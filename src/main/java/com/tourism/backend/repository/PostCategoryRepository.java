package com.tourism.backend.repository;

import com.tourism.backend.entity.PostCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Integer> {
    @Query("""
        SELECT c AS category, 
               COUNT(p) AS postCount 
        FROM PostCategory c 
        LEFT JOIN ForumPost p ON p.category = c AND p.status = 'PUBLISHED'
        WHERE c.isActive = true 
        GROUP BY c 
        ORDER BY c.displayOrder ASC
        """)
    List<Object[]> findAllActiveWithPostCount();

    @Query("""
        SELECT c AS category, 
               COUNT(p) AS postCount 
        FROM PostCategory c 
        LEFT JOIN ForumPost p ON p.category = c AND p.status = 'PUBLISHED'
        WHERE c.isActive = true 
        GROUP BY c 
        ORDER BY COUNT(p) DESC, c.displayOrder ASC
        """)
    List<Object[]> findPopularCategoriesWithCount(Pageable pageable);
}