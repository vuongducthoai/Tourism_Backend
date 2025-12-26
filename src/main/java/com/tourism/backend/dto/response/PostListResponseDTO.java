package com.tourism.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PostListResponseDTO {
    private Integer postID;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private String postType;

    // Author info
    private Integer authorId;
    private String authorName;
    private String authorAvatar;

    // Category info
    private Integer categoryId;
    private String categoryName;
    private String categorySlug;
    private String categoryColor;

    // Tags
    private List<TagInfo> tags;

    // Stats
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;

    // Flags
    private Boolean isPinned;
    private Boolean isFeatured;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagInfo {
        private Integer tagId;
        private String tagName;
        private String color;
    }
}
