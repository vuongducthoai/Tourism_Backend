package com.tourism.backend.controller;

import com.tourism.backend.dto.request.CommentRequestDTO;
import com.tourism.backend.dto.request.CreatePostRequestDTO;
import com.tourism.backend.dto.request.PostFilterRequest;
import com.tourism.backend.dto.response.ApiResponse;
import com.tourism.backend.dto.response.PostDetailResponseDTO;
import com.tourism.backend.dto.response.PostListResponseDTO;
import com.tourism.backend.service.ForumPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/forum/posts")
@RequiredArgsConstructor
public class ForumPostController {
    private final ForumPostService forumPostService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListResponseDTO>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer tagId,
            @RequestParam(required = false) String postType,
            @RequestParam(required = false) String search
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        PostFilterRequest filterRequest = PostFilterRequest.builder()
                .categoryId(categoryId)
                .tagId(tagId)
                .postType(postType)
                .search(search)
                .build();

        Page<PostListResponseDTO> posts = forumPostService.getPosts(filterRequest, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<PostListResponseDTO>>builder()
                .success(true)
                .message("Lấy danh sách bài viết thành công")
                .data(posts)
                .build());
    }

    /**
     * API 2: Lấy bài viết phổ biến (trending)
     * GET /api/forum/posts/trending?limit=5
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Page<PostListResponseDTO>>> getTrendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostListResponseDTO> posts = forumPostService.getTrendingPosts(pageable);

        return ResponseEntity.ok(ApiResponse.<Page<PostListResponseDTO>>builder()
                .success(true)
                .message("Lấy bài viết phổ biến thành công")
                .data(posts)
                .build());
    }

    /**
     * API 3: Lấy bài viết theo user
     * GET /api/forum/posts/user/{userId}?page=0&size=10
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostListResponseDTO>>> getPostsByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostListResponseDTO> posts = forumPostService.getPostsByUser(userId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<PostListResponseDTO>>builder()
                .success(true)
                .message("Lấy bài viết của người dùng thành công")
                .data(posts)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponseDTO>> createPost(
            @Valid @RequestBody CreatePostRequestDTO request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        PostDetailResponseDTO response = forumPostService.createPost(request, username);
        return ResponseEntity.ok(ApiResponse.<PostDetailResponseDTO>builder()
                .success(true)
                .message("Tạo bài viết thành công")
                .data(response)
                .build());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponseDTO>> getPostDetail(
            @PathVariable Integer postId,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String clientIp = getClientIpAddress(request);
        String username = authentication != null ? authentication.getName() : null;
        PostDetailResponseDTO post = forumPostService.getPostDetail(postId, username, clientIp);

        return ResponseEntity.ok(ApiResponse.<PostDetailResponseDTO>builder()
                .success(true)
                .message("Lấy chi tiết bài viết thành công")
                .data(post)
                .build());
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> togglePostLike(
            @PathVariable Integer postId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        forumPostService.togglePostLike(postId, username);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Thao tác like thành công")
                .data(null)
                .build());
    }

    @GetMapping("/{postId}/like-check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkPostLikeStatus(
            @PathVariable Integer postId,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(ApiResponse.<Map<String, Boolean>>builder()
                    .success(true)
                    .data(Map.of("isLiked", false))
                    .build());
        }

        String username = authentication.getName();
        boolean isLiked = forumPostService.checkPostLikeStatus(postId, username);

        return ResponseEntity.ok(ApiResponse.<Map<String, Boolean>>builder()
                .success(true)
                .message("Kiểm tra trạng thái like thành công")
                .data(Map.of("isLiked", isLiked))
                .build());
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<String>> toggleCommentLike(
            @PathVariable Integer commentId,
            Authentication authentication

    ) {
        String username = authentication.getName();

        forumPostService.toggleCommentLike(commentId, username);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Thao tác like bình luận thành công")
                .data(null)
                .build());
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<PostDetailResponseDTO>> addComment(
            @PathVariable Integer postId,
            @Valid @RequestBody CommentRequestDTO request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        PostDetailResponseDTO updatedPost = forumPostService.addComment(postId, request, username);

        return ResponseEntity.ok(ApiResponse.<PostDetailResponseDTO>builder()
                .success(true)
                .message("Bình luận thành công")
                .data(updatedPost)
                .build());
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
