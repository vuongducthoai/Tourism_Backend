package com.tourism.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.tourism.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import com.cloudinary.utils.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folderName) throws IOException {
        // Cấu hình tham số upload
        Map params = ObjectUtils.asMap(
                "folder", "tourism_app_tours/" + folderName, // Tên thư mục trên Cloudinary
                "resource_type", "auto"        // Tự động nhận diện (ảnh/video)
        );

        // Upload file lên Cloudinary
        // file.getBytes() chuyển đổi file từ request thành mảng byte để upload
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        // Trả về đường dẫn ảnh (URL) an toàn (https)
        return (String) uploadResult.get("secure_url");
    }
}
