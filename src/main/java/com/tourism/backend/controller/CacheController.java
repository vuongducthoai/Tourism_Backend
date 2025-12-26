package com.tourism.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
public class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    @DeleteMapping("/post-views/{postId}")
    public ResponseEntity<?> clearPostViewCache(@PathVariable Integer postId) {
        Set<String> keys = redisTemplate.keys("post:view:" + postId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            return ResponseEntity.ok("Cleared " + keys.size() + " view cache keys for post " + postId);
        }
        return ResponseEntity.ok("No cache found for post " + postId);
    }

    @DeleteMapping("/post-views")
    public ResponseEntity<?> clearAllPostViewCache() {
        Set<String> keys = redisTemplate.keys("post:view:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            return ResponseEntity.ok("Cleared " + keys.size() + " view cache keys");
        }
        return ResponseEntity.ok("No view cache found");
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> clearAllCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        return ResponseEntity.ok("All cache cleared");
    }
}