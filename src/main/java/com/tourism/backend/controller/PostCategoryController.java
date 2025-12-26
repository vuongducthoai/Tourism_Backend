package com.tourism.backend.controller;

import com.tourism.backend.dto.response.ApiResponse;
import com.tourism.backend.dto.response.CategoryResponse;
import com.tourism.backend.service.PostCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/categories")
@RequiredArgsConstructor
public class PostCategoryController {

    private final PostCategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllActiveCategories();

        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .success(true)
                .message("Lấy danh sách danh mục thành công")
                .data(categories)
                .build());
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getPopularCategories(
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        List<CategoryResponse> categories = categoryService.getPopularCategories(limit);

        return ResponseEntity.ok(ApiResponse.<List<CategoryResponse>>builder()
                .success(true)
                .message("Lấy danh mục phổ biến thành công")
                .data(categories)
                .build());
    }
}
