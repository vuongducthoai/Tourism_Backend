package com.tourism.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostFilterRequest {
    private Integer categoryId;
    private Integer tagId;
    private String postType;
    private String search;
}
