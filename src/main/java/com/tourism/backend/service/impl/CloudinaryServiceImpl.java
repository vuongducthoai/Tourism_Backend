package com.tourism.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.tourism.backend.exception.VideoUploadException;
import com.tourism.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cloudinary.utils.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folderName) throws IOException {
        Map params = ObjectUtils.asMap(
                "folder", "tourism_app_tours/" + folderName,
                "resource_type", "auto"
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        return (String) uploadResult.get("secure_url");
    }

    @Override
    public Map<String, Object> uploadVideo(MultipartFile file) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("resource_type", "video");
            params.put("folder", "tour-videos");
            params.put("chunk_size", 6000000);

            params.put("eager", List.of(
                    new com.cloudinary.Transformation()
                            .startOffset("0")
                            .duration("2")
                            .crop("fill")
                            .width(640)
                            .height(360)
            ));

            Map<String, Object> result = cloudinary.uploader()
                    .upload(file.getBytes(), params);

            List<Map<String, Object>> eagerList =
                    (List<Map<String, Object>>) result.get("eager");

            String thumbnailUrl = eagerList.get(0).get("secure_url").toString();
            Double duration = (Double) result.get("duration");

            return Map.of(
                    "secure_url", result.get("secure_url"),
                    "thumbnail_url", thumbnailUrl,
                    "duration", duration,
                    "public_id", result.get("public_id")
            );

        } catch (Exception e) {
            throw new VideoUploadException("Failed to upload video: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        if(imageUrl == null || imageUrl.isEmpty()){
            return;
        }

        String publicId = getPublicIdFromUrl(imageUrl);

        if(publicId != null){
            log.info("Deleting image from Cloudinary with public_id: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        }
    }

    @Override
    public void deleteImageByPublicId(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
    }

    public String getPublicIdFromUrl(String url) {
        // Regex như bạn đã có
        Pattern pattern = Pattern.compile(".*/upload/(?:v\\d+/)?([^.]+)\\.[a-z]+$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
