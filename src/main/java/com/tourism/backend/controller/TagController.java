package com.tourism.backend.controller;

import com.tourism.backend.dto.response.ApiResponse;
import com.tourism.backend.dto.response.TagResponse;
import com.tourism.backend.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    /**
     * API 1: Lấy tất cả tags (có phân trang)
     * GET /api/forum/tags?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TagResponse>>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "tagName") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<TagResponse> tags = tagService.getAllActiveTags(pageable);

        return ResponseEntity.ok(ApiResponse.<Page<TagResponse>>builder()
                .success(true)
                .message("Lấy danh sách tags thành công")
                .data(tags)
                .build());
    }

    /**
     * API 2: Lấy tags phổ biến (được sử dụng nhiều nhất)
     * GET /api/forum/tags/popular?limit=20
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getPopularTags(
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        List<TagResponse> tags = tagService.getPopularTags(limit);

        return ResponseEntity.ok(ApiResponse.<List<TagResponse>>builder()
                .success(true)
                .message("Lấy tags phổ biến thành công")
                .data(tags)
                .build());
    }
}
