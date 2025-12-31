package com.tourism.backend.controller;

import com.tourism.backend.dto.response.FollowerResponseDTO;
import com.tourism.backend.service.FollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/followers")
@RequiredArgsConstructor
public class FollowerController {
    private final FollowerService followerService;

    @PostMapping("/{userId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @PathVariable Integer userId,
            Authentication authentication) {

        String username = authentication.getName();
        followerService.toggleFollow(userId, username);

        boolean isFollowing = followerService.isFollowing(userId, username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFollowing", isFollowing);
        response.put("message", isFollowing ? "Đã follow thành công" : "Đã unfollow");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkFollowing(
            @PathVariable Integer userId,
            Authentication authentication) {

        String username = authentication.getName();
        boolean isFollowing = followerService.isFollowing(userId, username);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isFollowing", isFollowing);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<FollowerResponseDTO>> getFollowers(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FollowerResponseDTO> followers = followerService.getFollowers(userId, pageable);

        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<FollowerResponseDTO>> getFollowing(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FollowerResponseDTO> following = followerService.getFollowing(userId, pageable);

        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Long>> getFollowerStats(@PathVariable Integer userId) {
        Long followerCount = followerService.getFollowerCount(userId);
        Long followingCount = followerService.getFollowingCount(userId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("followers", followerCount);
        stats.put("following", followingCount);

        return ResponseEntity.ok(stats);
    }

}
