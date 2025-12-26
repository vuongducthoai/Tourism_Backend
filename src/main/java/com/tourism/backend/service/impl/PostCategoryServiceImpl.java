package com.tourism.backend.service.impl;

import com.tourism.backend.dto.response.CategoryResponse;
import com.tourism.backend.entity.PostCategory;
import com.tourism.backend.repository.PostCategoryRepository;
import com.tourism.backend.service.PostCategoryService;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCategoryServiceImpl implements PostCategoryService {
    private final PostCategoryRepository categoryRepository;

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getAllActiveCategories() {
        List<Object[]> results = categoryRepository.findAllActiveWithPostCount();

        return results.stream()
                .map(row -> {
                    PostCategory category = (PostCategory) row[0];
                    Long postCount = (Long) row[1];

                    return mapToCategoryResponse(category, postCount.intValue());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "popularCategories", key = "#limit")
    public List<CategoryResponse> getPopularCategories(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Object[]> results = categoryRepository.findPopularCategoriesWithCount(pageRequest);

        return results.stream()
                .map(row -> {
                    PostCategory category = (PostCategory) row[0];
                    Long postCount = (Long) row[1];

                    return mapToCategoryResponse(category, postCount.intValue());
                })
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToCategoryResponse(PostCategory category, int postCount) {
        return CategoryResponse.builder()
                .categoryID(category.getCategoryID())
                .categoryName(category.getCategoryName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .icon(category.getIcon())
                .color(category.getColor())
                .postCount(postCount)
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
