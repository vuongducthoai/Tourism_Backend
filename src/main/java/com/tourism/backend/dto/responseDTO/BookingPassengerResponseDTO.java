package com.tourism.backend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPassengerResponseDTO {
    private Integer bookingPassengerID;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String passengerType;
    private BigDecimal basePrice;
    private Boolean requiresSingleRoom = false;
    private BigDecimal singleRoomSurcharge;
}
