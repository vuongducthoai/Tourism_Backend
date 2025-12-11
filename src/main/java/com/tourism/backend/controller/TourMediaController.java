package com.tourism.backend.controller;

import com.tourism.backend.dto.request.TourMediaDTO;
import com.tourism.backend.service.TourMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tours/{tourId}/media")
@RequiredArgsConstructor
public class TourMediaController {
    private final TourMediaService tourMediaService;
    @PostMapping("/videos")
    public ResponseEntity<TourMediaDTO> uploadVideo(
            @PathVariable Integer tourId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {
        TourMediaDTO media = tourMediaService.uploadVideo(tourId, file, title, description);
        return ResponseEntity.ok(media);
    }
}
