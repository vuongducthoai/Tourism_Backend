package com.tourism.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponseDTO {
    private String bookingCode;
    private LocalDateTime createdDate;
    private BigDecimal originalPrice;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private String status;
    private LocalDateTime paymentDeadline;

    private List<PassengerDTO> passengers;
    private List<String> appliedCouponCodes;

    private String tourName;
    private String tourImage;
    private String tourCode;
    private String duration;

    private BookingFlightDTO outboundTransport;
    private BookingFlightDTO inboundTransport;

    @Data
    @Builder
    public static class PassengerDTO {
        private String fullName;
        private String dateOfBirth; // dd/MM/yyyy
        private String gender;
        private String type;        // Người lớn, Trẻ em
        private Boolean singleRoom;
    }
}
