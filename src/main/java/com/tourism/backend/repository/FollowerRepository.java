package com.tourism.backend.repository;

import com.tourism.backend.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    // Kiểm tra user A có follow user B không
    @Query("SELECT f FROM Follower f WHERE f.followerUser.userID = :followerId AND f.following.userID = :followingId")
    Optional<Follower> findByFollowerAndFollowing(@Param("followerId") Integer followerId,
                                                  @Param("followingId") Integer followingId);

    // Đếm số người follow user
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.following.userID = :userId")
    Long countFollowersByUserId(@Param("userId") Integer userId);

    // Đếm số người mà user đang follow
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.followerUser.userID = :userId")
    Long countFollowingByUserId(@Param("userId") Integer userId);

    // Lấy danh sách followers của user
    @Query("SELECT f FROM Follower f WHERE f.following.userID = :userId ORDER BY f.followedAt DESC")
    List<Follower> findFollowersByUserId(@Param("userId") Integer userId);

    // Lấy danh sách user mà user đang follow
    @Query("SELECT f FROM Follower f WHERE f.followerUser.userID = :userId ORDER BY f.followedAt DESC")
    List<Follower> findFollowingByUserId(@Param("userId") Integer userId);

    // Lấy danh sách user IDs của những người follow một user (để gửi notification)
    @Query("SELECT f.followerUser.userID FROM Follower f WHERE f.following.userID = :userId")
    List<Integer> findFollowerUserIdsByUserId(@Param("userId") Integer userId);
}
