package com.tourism.backend.controller;

import com.tourism.backend.dto.response.ApiResponse;
import com.tourism.backend.dto.response.UploadImageResponseDTO;
import com.tourism.backend.entity.ForumPost;
import com.tourism.backend.entity.PostImage;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.ForumPostRepository;
import com.tourism.backend.repository.PostImageRepository;
import com.tourism.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadPostImageController {
    private final CloudinaryService cloudinaryService;
    private final ForumPostRepository postRepository;
    private final PostImageRepository postImageRepository;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderName", defaultValue = "forum_posts") String folderName
    ) {
        try {
            String url = cloudinaryService.uploadImage(file, folderName);
            String publicId = cloudinaryService.getPublicIdFromUrl(url);

            Map<String, String> data = Map.of(
                    "imageUrl", url,
                    "publicId", publicId
            );

            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                    .success(true)
                    .message("Upload ảnh thành công")
                    .data(data)
                    .build());
        } catch (Exception e) {
            log.error("Upload ảnh thất bại", e);
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage());
        }
    }

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> deleteImage(
            @PathVariable Integer imageId,
            Authentication authentication
    ) {
        PostImage postImage = postImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Ảnh không tồn tại"));

        // Xóa trên Cloudinary
        try {
            if (postImage.getPublicId() != null) {
                cloudinaryService.deleteImageByPublicId(postImage.getPublicId());
            }
        } catch (Exception e) {
            log.warn("Không thể xóa ảnh trên Cloudinary: {}", e.getMessage());
        }

        // Xóa trong DB
        postImageRepository.delete(postImage);

        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Xóa ảnh thành công")
                .data(Map.of("deletedImageId", imageId.toString()))
                .build());
    }

}
