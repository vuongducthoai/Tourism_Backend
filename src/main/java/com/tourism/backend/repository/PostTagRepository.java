package com.tourism.backend.repository;

import com.tourism.backend.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Integer> {
}
