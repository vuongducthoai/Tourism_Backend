package com.tourism.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourGeneralInfoRequest {
    @NotBlank(message = "Tên tour không được để trống")
    @Size(min = 10, max = 255, message = "Tên tour phải từ 10 đến 255 ký tự")
    private String tourName;

    @NotBlank(message = "Mã tour không được để trống")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Mã tour chỉ chứa chữ hoa, số và dấu gạch ngang")
    private String tourCode;

    @NotBlank(message = "Thời gian không được để trống")
    private String duration;

    @NotBlank(message = "Phương tiện không được để trống")
    private String transportation;

    @NotNull(message = "Điểm khởi hành không được để trống")
    private Integer startLocationId;

    @NotNull(message = "Điểm đến không được để trống")
    private Integer endLocationId;

    @NotBlank(message = "Điểm tham quan không được để trống")
    private String attractions;

    private String meals;
    private String idealTime;
    private String tripTransportation;
    private String suitableCustomer;
    private String hotel;
    private Boolean status;
}
