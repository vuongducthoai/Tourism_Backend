package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookmarkResponseDTO {
    private Integer bookmarkId;
    private Integer postId;
    private String postTitle;
    private String postSummary;
    private String postThumbnail;
    private String authorName;
    private String folderName;
    private String note;
    private LocalDateTime bookmarkedAt;
}
