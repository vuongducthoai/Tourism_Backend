package com.tourism.backend.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequestDTO {
    private  Integer tourId;
    private Integer departureId;

    //Contact
    private String contactFullName;
    private String contactEmail;
    private String contactPhone;
    private String contactAddress;
    private String customerNote;

    //Lit passenger
    private List<PassengerRequest> passengers;

    private List<String> couponCode;
    private Integer pointsUsed;

    @Data
    public static class PassengerRequest {
        private String fullName;
        private String gender;
        private LocalDate dateOfBirth;
        private String type;
        private boolean singleRoom;
    }
}
