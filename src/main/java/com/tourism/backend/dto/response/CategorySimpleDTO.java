package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySimpleDTO {
    private Integer categoryId;
    private String categoryName;
    private String slug;
    private String icon;
    private String color;
}