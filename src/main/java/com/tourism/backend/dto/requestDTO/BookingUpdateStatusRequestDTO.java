package com.tourism.backend.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingUpdateStatusRequestDTO {
    private Integer bookingID;
    private String bookingStatus;
    private String cancelReason;
}
