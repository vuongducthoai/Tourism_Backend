package com.tourism.backend.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String provinceCode;
    private String provinceName;
    private String districtCode;
    private String districtName;
    private String avatar;
}

