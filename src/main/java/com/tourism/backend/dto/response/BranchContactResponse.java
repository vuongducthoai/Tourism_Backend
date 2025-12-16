package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchContactResponse {
    private Integer contactID;
    private String branchName;
    private String phone;
    private String email;
    private String address;
    private Boolean isHeadOffice;
    private Integer policyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
