package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagSimpleDTO {
    private Integer tagId;
    private String tagName;
    private String slug;
    private String color;
}