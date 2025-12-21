package com.tourism.backend.dto.responseDTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserReaponseDTO {
    private Integer userID;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private BigDecimal coinBalance;
    private String email;
    private String avatar;
    private Boolean status;
    private LocalDateTime lastActiveAt;
    private String activityStatus;
}

