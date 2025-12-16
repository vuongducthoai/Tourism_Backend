package com.tourism.backend.service.impl;

import com.tourism.backend.dto.request.TourMediaDTO;
import com.tourism.backend.dto.response.TourImageResponse;
import com.tourism.backend.entity.Tour;
import com.tourism.backend.entity.TourImage;
import com.tourism.backend.entity.TourMedia;
import com.tourism.backend.exception.FileSizeExceededException;
import com.tourism.backend.exception.InvalidFileException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.repository.TourImageRepository;
import com.tourism.backend.repository.TourMediaRepository;
import com.tourism.backend.repository.TourRepository;
import com.tourism.backend.service.CloudinaryService;
import com.tourism.backend.service.TourMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TourMediaServiceImpl implements TourMediaService {
    private final TourMediaRepository tourMediaRepository;
    private final TourRepository tourRepository;
    private final CloudinaryService cloudinaryService;
    private final TourImageRepository tourImageRepository;

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

    public TourImageResponse uploadImage(Integer tourId, MultipartFile file, Boolean isMainImage) throws IOException {
        if (!isImageFile(file)) {
            throw new InvalidFileException("File phải là định dạng hình ảnh (jpg, png, jpeg, webp...)");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // Giới hạn 5MB cho ảnh
            throw new FileSizeExceededException("Kích thước ảnh không được vượt quá 5MB");
        }

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour với ID: " + tourId));

        String subFolder = tour.getTourCode();
        if(subFolder == null) subFolder = "unknown_tour";
        String imageUrl = cloudinaryService.uploadImage(file, subFolder);

        boolean setAsMain = Boolean.TRUE.equals(isMainImage);
        if (tour.getImages().isEmpty()) {
            setAsMain = true;
        }

        if (setAsMain) {
            List<TourImage> existingImages = tourImageRepository.findByTour(tour);
            for (TourImage img : existingImages) {
                if (Boolean.TRUE.equals(img.getIsMainImage())) {
                    img.setIsMainImage(false);
                    tourImageRepository.save(img);
                }
            }
        }

        TourImage tourImage = new TourImage();
        tourImage.setTour(tour);
        tourImage.setImageURL(imageUrl);
        tourImage.setIsMainImage(setAsMain);

        TourImage savedImage = tourImageRepository.save(tourImage);

        return TourImageResponse.builder()
                .imageID(savedImage.getImageID())
                .imageURL(savedImage.getImageURL())
                .isMainImage(savedImage.getIsMainImage())
                .build();
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
}
