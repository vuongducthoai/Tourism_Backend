package com.tourism.backend.dto.response;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class UserInfo {
        private Integer userId;
        private String fullName;
        private String email;
        private String role;
        private String provinceName;
        private String districtName;
        private BigDecimal coinBalance;
    }
}
