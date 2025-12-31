package com.tourism.backend.repository;


import com.tourism.backend.entity.PostBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Integer> {
    // Kiểm tra user đã bookmark post chưa
    @Query("SELECT b FROM PostBookmark b WHERE b.user.userID = :userId AND b.post.postID = :postId")
    Optional<PostBookmark> findByUserAndPost(@Param("userId") Integer userId,
                                             @Param("postId") Integer postId);

    // Lấy tất cả bookmarks của user
    @Query("SELECT b FROM PostBookmark b WHERE b.user.userID = :userId ORDER BY b.createdAt DESC")
    Page<PostBookmark> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    // Lấy bookmarks theo folder
    @Query("SELECT b FROM PostBookmark b WHERE b.user.userID = :userId AND b.folderName = :folderName ORDER BY b.createdAt DESC")
    Page<PostBookmark> findByUserIdAndFolder(@Param("userId") Integer userId,
                                             @Param("folderName") String folderName,
                                             Pageable pageable);

    // Đếm số bookmark của user
    @Query("SELECT COUNT(b) FROM PostBookmark b WHERE b.user.userID = :userId")
    Long countByUserId(@Param("userId") Integer userId);

    // Lấy danh sách folders của user
    @Query("SELECT DISTINCT b.folderName FROM PostBookmark b WHERE b.user.userID = :userId AND b.folderName IS NOT NULL")
    List<String> findFoldersByUserId(@Param("userId") Integer userId);
}
