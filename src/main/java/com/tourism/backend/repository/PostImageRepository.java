package com.tourism.backend.repository;

import com.tourism.backend.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPostPostIDOrderByDisplayOrderAsc(Integer postId);

    void deleteByPublicId(String publicId);
}