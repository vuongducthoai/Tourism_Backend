package com.tourism.backend.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartureRequest {
    @NotNull(message = "Tour ID không được để trống")
    private Integer tourId;

    @NotNull(message = "Ngày khởi hành không được để trống")
    @Future(message = "Ngày khởi hành phải là ngày trong tương lai")
    private LocalDate departureDate;

    @NotNull(message = "Số chỗ trống không được để trống")
    @Min(value = 1, message = "Số chỗ trống phải >= 1")
    private Integer availableSlots;

    @NotBlank(message = "Thông tin hướng dẫn viên không được để trống")
    private String tourGuideInfo;

    @NotNull(message = "Policy template ID không được để trống")
    private Integer policyTemplateId;

    private Integer couponId;

    @Valid
    @NotNull(message = "Danh sách giá không được để trống")
    private List<DeparturePricingRequest> pricings;

    @Valid
    private DepartureTransportRequest outboundTransport;

    @Valid
    private DepartureTransportRequest inboundTransport;
}
