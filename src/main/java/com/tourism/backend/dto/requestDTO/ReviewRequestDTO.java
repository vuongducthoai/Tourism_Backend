package com.tourism.backend.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDTO {
    private Integer rating;
    private String comment;
    List<MultipartFile> images;
    private Integer tourID;
    private Integer bookingID;
}
