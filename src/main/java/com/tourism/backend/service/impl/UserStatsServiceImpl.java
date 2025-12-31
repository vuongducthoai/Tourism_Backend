package com.tourism.backend.service.impl;

import com.tourism.backend.dto.response.UserStatsResponseDTO;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.ContentStatus;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.*;
import com.tourism.backend.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final UserRepository userRepository;
    private final ForumPostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final FollowerRepository followerRepository;

    @Override
    public UserStatsResponseDTO getUserStats(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + username));

        // Đếm số bài viết đã publish của user
        long totalPosts = postRepository.countByUserAndStatus(user, ContentStatus.PUBLISHED);

        // Đếm số bình luận của user
        long totalComments = commentRepository.countByUser(user);

        //Đếm số follower của user
        long totalFollowers = followerRepository.countFollowersByUserId(user.getUserID());

        // Đếm số like mà các bài viết của user nhận được
        long totalLikesReceived = likeRepository.countByPostUser(user);

        // Tính điểm uy tín (có thể tùy chỉnh công thức)
        int reputationPoints = calculateReputation((int) totalPosts, (int) totalComments, (int) totalLikesReceived, (int)totalFollowers);

        return UserStatsResponseDTO.builder()
                .totalPosts((int) totalPosts)
                .totalComments((int) totalComments)
                .totalLikesReceived((int) totalLikesReceived)
                .reputationPoints(reputationPoints)
                .totalFollowers((int)totalFollowers)
                .build();
    }

    private int calculateReputation(int posts, int comments, int likes, int followers) {
        return posts * 10 + comments * 2 + likes * 5 + followers * 3;
    }
}