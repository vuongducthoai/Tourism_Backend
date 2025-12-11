package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.TourMediaDTO;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourMedia;
import com.tourism.backend.exception.FileSizeExceededException;
import com.tourism.backend.exception.InvalidFileException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.TourMediaRepository;
import com.tourism.backend.repository.TourRepository;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.TourMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TourMediaServiceImpl implements TourMediaService {
    private final TourMediaRepository tourMediaRepository;
    private final TourRepository tourRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public TourMediaDTO uploadVideo(Integer tourId, MultipartFile file, String title, String description) {
        if(!isVideoFile(file)){
            throw new InvalidFileException("File must be a video");
        }

        if (file.getSize() > 100 * 1024 * 1024) { // 100MB limit
            throw new FileSizeExceededException("Video size must be less than 100MB");
        }

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found"));

        // Upload to Cloudinary
        Map<String, Object> uploadResult = cloudinaryService.uploadVideo(file);

        TourMedia media = TourMedia.builder()
                .tour(tour)
                .mediaUrl((String) uploadResult.get("secure_url"))
                .thumbnailUrl((String) uploadResult.get("thumbnail_url"))
                .title(title)
                .description(description)
                .duration((Double) uploadResult.get("duration"))
                .fileSize(file.getSize())
                .isPrimary(tour.getMediaList().isEmpty()) // First video = primary
                .build();

        tourMediaRepository.save(media);
        return TourMediaDTO.builder()
                .mediaUrl(media.getMediaUrl())
                .description(media.getDescription())
                .title(media.getTitle())
                .duration(media.getDuration())
                .fileSize(media.getFileSize())
                .build();
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
}
