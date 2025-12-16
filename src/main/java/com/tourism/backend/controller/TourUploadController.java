package com.tourism.backend.controller;

import com.tourism.backend.service.impl.TourMediaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tours")
@RequiredArgsConstructor
public class TourUploadController {
    private final TourMediaServiceImpl tourMediaService;

    @PostMapping(value = "/{tourId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTourImage(
            @PathVariable Integer tourId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isMain", defaultValue = "false") Boolean isMain) throws IOException {

        var response = tourMediaService.uploadImage(tourId, file, isMain);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @PostMapping(value = "/{tourId}/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTourVideo(
            @PathVariable Integer tourId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description) {

        var response = tourMediaService.uploadVideo(tourId, file, title, description);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }
}
