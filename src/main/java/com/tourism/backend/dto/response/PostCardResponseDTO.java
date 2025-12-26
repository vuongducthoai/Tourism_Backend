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
public class PostCardResponseDTO {

    private Integer postId;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private PostType postType;
    private ContentStatus status;

    // Author info (minimal)
    private Integer authorId;
    private String authorName;
    private String authorAvatar;

    // Category & Tags (minimal)
    private CategorySimpleDTO category;
    private List<String> tagNames;

    // Statistics
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    // Flags
    private Boolean isPinned;
    private Boolean isFeatured;

    // User interactions
    private Boolean isLikedByCurrentUser;
    private Boolean isBookmarkedByCurrentUser;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
