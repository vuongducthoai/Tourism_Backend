package com.tourism.backend.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundInformationRequestDTO {
    private String accountName;
    private String accountNumber;
    private String bank;
}
