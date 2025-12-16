package com.tourism.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDayRequest {
    @NotNull(message = "Số ngày không được để trống")
    @Min(value = 1, message = "Số ngày phải từ 1 trở lên")
    private Integer dayNumber;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không quá 200 ký tự")
    private String title;

    @NotBlank(message = "Thông tin bữa ăn không được để trống")
    private String meals;

    @NotBlank(message = "Chi tiết lịch trình không được để trống")
    private String details;
}
