package com.tourism.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrachContactDTO {
    private Integer contactID;
    private String branchName;
    private String phone;
    private String email;
    private String address;
    private Boolean isHeadOffice = false;
}
