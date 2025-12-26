package com.tourism.backend.repository;

import com.tourism.backend.entity.PostLike;
import com.tourism.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    @Query("SELECT COUNT(l) FROM PostLike l WHERE l.post.user = :user")
    long countByPostUser(@Param("user") User user);
}
