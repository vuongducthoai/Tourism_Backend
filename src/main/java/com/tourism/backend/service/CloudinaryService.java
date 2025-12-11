package com.tourism.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    String uploadImage(MultipartFile file, String folder) throws IOException;
    Map<String, Object> uploadVideo(MultipartFile file);
}
