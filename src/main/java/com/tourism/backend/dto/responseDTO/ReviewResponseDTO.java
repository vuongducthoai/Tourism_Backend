package com.tourism.backend.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private Integer reviewID;
    private Integer rating;
    private String comment;
    private String bookingCode; // Hoặc bất kỳ thông tin cần thiết nào khác
    private String tourCode;
    private List<String> imageUrls; // List URL ảnh
}
