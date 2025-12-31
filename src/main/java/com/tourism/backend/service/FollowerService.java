package com.tourism.backend.service;

import com.tourism.backend.dto.response.FollowerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowerService {
    void toggleFollow(Integer targetUserId, String username);
    boolean isFollowing(Integer targetUserId, String username);
    Page<FollowerResponseDTO> getFollowers(Integer userId, Pageable pageable);
    Page<FollowerResponseDTO> getFollowing(Integer userId, Pageable pageable);
    Long getFollowerCount(Integer userId);
    Long getFollowingCount(Integer userId);
}
