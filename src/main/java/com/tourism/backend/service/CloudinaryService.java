package com.tourism.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    public String uploadImage(MultipartFile file, String folder) throws IOException;
}
