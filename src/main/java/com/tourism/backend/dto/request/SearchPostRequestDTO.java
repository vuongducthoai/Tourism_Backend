package com.tourism.backend.dto.request;

import com.tourism.backend.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostRequestDTO {

    private String keyword; // Tìm kiếm theo title hoặc content

    private Integer categoryId;

    private List<Integer> tagIds;

    private PostType postType;

    private Integer authorId; // Tìm theo tác giả

    private Integer tourId;

    private Boolean isPinned;

    private Boolean isFeatured;

    // Paging & Sorting
    private Integer page = 0;

    private Integer size = 20;

    private String sortBy = "createdAt"; // createdAt, viewCount, likeCount, commentCount

    private String sortDirection = "DESC"; // ASC or DESC
}