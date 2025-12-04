package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponseDTO {
    private Integer userId;
    private String fullName;
    private String email;
    private String provinceName;
    private String districtName;
    private String message;
}
