package com.tourism.backend.service;

import com.tourism.backend.dto.request.CommentRequestDTO;
import com.tourism.backend.dto.request.CreatePostRequestDTO;
import com.tourism.backend.dto.request.PostFilterRequest;
import com.tourism.backend.dto.response.PostDetailResponseDTO;
import com.tourism.backend.dto.response.PostListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ForumPostService {
    Page<PostListResponseDTO> getPosts(PostFilterRequest filter, Pageable pageable);
    Page<PostListResponseDTO> getTrendingPosts(Pageable pageable);
    Page<PostListResponseDTO> getPostsByUser(Integer userId, Pageable pageable);
    PostDetailResponseDTO createPost(CreatePostRequestDTO request, String username);
    PostDetailResponseDTO getPostDetail(Integer postId, String username, String clientIp);
    void togglePostLike(Integer postId, String username);
    PostDetailResponseDTO addComment(Integer postId, CommentRequestDTO request, String username);
    void toggleCommentLike(Integer commentId, String username);
}
