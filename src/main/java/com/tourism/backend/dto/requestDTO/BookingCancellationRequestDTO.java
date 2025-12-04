// File: com.tourism.backend.dto.requestDTO.BookingCancellationRequestDTO.java
package com.tourism.backend.dto.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingCancellationRequestDTO {
    @NotNull(message = "Booking ID is required")
    private Integer bookingID;

    // Có thể thêm lý do hủy nếu cần
    // private String cancelReason;
}