package com.tourism.backend.repository;

import com.tourism.backend.entity.PostComment;
import com.tourism.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
    long countByUser(User user);
}
