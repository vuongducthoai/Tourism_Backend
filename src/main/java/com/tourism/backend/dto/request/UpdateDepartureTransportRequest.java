package com.tourism.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartureTransportRequest {
    @NotBlank(message = "Direction không được để trống")
    @Pattern(regexp = "outbound|inbound", message = "Direction phải là 'outbound' hoặc 'inbound'")
    private String direction;

    @Valid
    @NotNull(message = "Thông tin vận chuyển không được để trống")
    private DepartureTransportRequest transport;
}
