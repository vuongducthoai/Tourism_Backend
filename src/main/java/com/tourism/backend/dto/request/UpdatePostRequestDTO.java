package com.tourism.backend.dto.request;

import com.tourism.backend.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 300, message = "Tiêu đề không được vượt quá 300 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @Size(max = 200, message = "Tóm tắt không được vượt quá 200 ký tự")
    private String summary;

    @NotNull(message = "Category ID không được để trống")
    private Integer categoryId;

    private Integer tourId;

    @NotNull(message = "Loại bài viết không được để trống")
    private PostType postType;

    private List<String> tagNames;

    private List<MultipartFile> newImages;

    private List<Integer> removeImageIds;

    private String thumbnailUrl;
}