package com.tourism.backend.service;

import com.tourism.backend.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    Page<TagResponse> getAllActiveTags(Pageable pageable);
    List<TagResponse> getPopularTags(Integer limit);
}
