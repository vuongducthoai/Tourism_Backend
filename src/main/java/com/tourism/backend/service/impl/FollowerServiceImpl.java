package com.tourism.backend.service.impl;

import com.tourism.backend.dto.response.FollowerResponseDTO;
import com.tourism.backend.entity.Follower;
import com.tourism.backend.entity.User;
import com.tourism.backend.enums.NotificationType;
import com.tourism.backend.exception.ResourceInUseException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.FollowerRepository;
import com.tourism.backend.repository.UserRepository;
import com.tourism.backend.service.FollowerService;
import com.tourism.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FollowerServiceImpl implements FollowerService {
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void toggleFollow(Integer targetUserId, String username) {
        User followerUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User targetUser  = userRepository.findByUserID(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        if(followerUser.getUserID().equals(targetUserId)){
            throw new IllegalArgumentException("Can't follow myself");
        }

        Optional<Follower> existingFollow = followerRepository
                .findByFollowerAndFollowing(followerUser.getUserID(), targetUserId);

        if(existingFollow.isPresent()){
            followerRepository.delete(existingFollow.get());
            log.info("User {} unfollowed user {}", followerUser.getUserID(), targetUserId);
        } else {
            Follower newFollow = new Follower();
            newFollow.setFollowerUser(followerUser);
            newFollow.setFollowing(targetUser);
            newFollow.setFollowedAt(LocalDateTime.now());
            followerRepository.save(newFollow);

            log.info("User {} followed user {}", followerUser.getUserID(), targetUserId);

            notifyNewFollower(targetUser, followerUser);
        }
    }

    private void notifyNewFollower(User targetUser, User followerUser) {
        try {
            String title = "Người theo dõi mới";
            String message = String.format("%s đã bắt đầu theo dõi bạn",
                    followerUser.getFullName());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("followerId", followerUser.getUserID());
            metadata.put("followerName", followerUser.getFullName());
            metadata.put("followerAvatar", followerUser.getAvatar());

            notificationService.createNotification(
                    targetUser.getUserID(),
                    NotificationType.NEW_FOLLOWER,
                    title,
                    message,
                    metadata
            );

            log.info("Follow notification sent to user {}", targetUser.getUserID());
        } catch (Exception e) {
            log.error("Failed to send follow notification: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Integer targetUserId, String username) {
        User user = userRepository.findByEmail(username).orElse(null);
        if(user == null) return false;

        return followerRepository.findByFollowerAndFollowing(user.getUserID(), targetUserId)
                .isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowerResponseDTO> getFollowers(Integer userId, Pageable pageable) {
        List<Follower> followers = followerRepository.findFollowersByUserId(userId);

        List<FollowerResponseDTO> dtos = followers.stream()
                .map(f -> FollowerResponseDTO.builder()
                        .userId(f.getFollowerUser().getUserID())
                        .fullName(f.getFollowerUser().getFullName())
                        .email(f.getFollowerUser().getEmail())
                        .avatarUrl(f.getFollowerUser().getAvatar())
                        .followedAt(f.getFollowedAt())
                        .build())
                .collect(Collectors.toList());

        int start = (int)pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());

    }

    @Override
    @Transactional(readOnly = true)
    public Page<FollowerResponseDTO> getFollowing(Integer userId, Pageable pageable) {
        List<Follower> following = followerRepository.findFollowingByUserId(userId);
        List<FollowerResponseDTO> dtos = following.stream()
                .map(f -> FollowerResponseDTO.builder()
                        .userId(f.getFollowing().getUserID())
                        .fullName(f.getFollowing().getFullName())
                        .email(f.getFollowing().getEmail())
                        .avatarUrl(f.getFollowing().getAvatar())
                        .followedAt(f.getFollowedAt())
                        .build())
                .collect(Collectors.toList());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }


    @Override
    @Transactional(readOnly = true)
    public Long getFollowerCount(Integer userId) {
        return followerRepository.countFollowingByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFollowingCount(Integer userId) {
        return followerRepository.countFollowersByUserId(userId);
    }
}
