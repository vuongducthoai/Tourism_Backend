package com.tourism.backend.service;

import com.tourism.backend.dto.response.CategoryResponse;

import java.util.List;

public interface PostCategoryService {
    List<CategoryResponse> getAllActiveCategories();
    List<CategoryResponse> getPopularCategories(Integer limit);
}
