package com.tourism.backend.dto.response;

import com.tourism.backend.enums.ContentStatus;
import com.tourism.backend.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDTO {

    private Integer postId;
    private String title;
    private String content;
    private String summary;
    private String thumbnailUrl;
    private String postType;
    private String status;

    // Author info
    private AuthorInfoDTO author;

    // Category & Tags
    private CategorySimpleDTO category;
    private List<TagSimpleDTO> tags;

    // Tour info (if linked)
    private TourSimpleDTO tour;

    // Statistics
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;
    private Integer shareCount;

    // Flags
    private Boolean isPinned;
    private Boolean isFeatured;

    // User interactions (cho user hiện tại)
    private Boolean isLikedByCurrentUser;
    private Boolean isBookmarkedByCurrentUser;
    private Boolean isFollowingAuthor;

    // Images
    private List<PostImageDTO> images;
    private List<CommentResponseDTO> comments;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
