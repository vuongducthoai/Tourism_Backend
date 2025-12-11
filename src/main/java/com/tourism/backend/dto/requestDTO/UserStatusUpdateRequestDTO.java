package com.tourism.backend.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateRequestDTO {
    private Integer userID;
    private Boolean status; // true = active, false = locked
    private String reason;
}