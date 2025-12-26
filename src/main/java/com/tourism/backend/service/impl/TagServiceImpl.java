package com.tourism.backend.service.impl;

import com.tourism.backend.dto.response.TagResponse;
import com.tourism.backend.entity.Tag;
import com.tourism.backend.repository.TagRepository;
import com.tourism.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public Page<TagResponse> getAllActiveTags(Pageable pageable) {
        List<Object[]> results = tagRepository.findAllActiveWithUsageCount(pageable);

        List<TagResponse> content = results.stream()
                .map(row -> {
                    Tag tag = (Tag) row[0];
                    Long count = (Long) row[1];
                    return mapToTagResponse(tag, count.intValue());
                })
                .collect(Collectors.toList());

        long total = tagRepository.countByIsActiveTrue();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    @Cacheable(value = "popularTags", key = "#limit")
    public List<TagResponse> getPopularTags(Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Object[]> results = tagRepository.findPopularTagsWithCount(pageRequest);

        return results.stream()
                .map(row -> {
                    Tag tag = (Tag) row[0];
                    Long count = (Long) row[1];
                    return mapToTagResponse(tag, count.intValue());
                })
                .collect(Collectors.toList());
    }

    private TagResponse mapToTagResponse(Tag tag, int usageCount) {
        return TagResponse.builder()
                .tagID(tag.getTagID())
                .tagName(tag.getTagName())
                .slug(tag.getSlug())
                .description(tag.getDescription())
                .color(tag.getColor())
                .usageCount(usageCount)
                .isActive(tag.getIsActive())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}
