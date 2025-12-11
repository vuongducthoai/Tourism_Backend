package com.tourism.backend.service;

import com.tourism.backend.dto.request.AddVideoUrlRequestDTO;
import com.tourism.backend.dto.request.TourMediaDTO;
import org.springframework.web.multipart.MultipartFile;

public interface TourMediaService {
    TourMediaDTO uploadVideo(Integer tourId, MultipartFile file,
                             String title, String description);

//    TourMediaDTO addVideoUrl(Integer tourId, AddVideoUrlRequestDTO request);
}
