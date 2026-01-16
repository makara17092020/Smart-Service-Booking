package com.mekong.smart_service_booking.service;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mekong.smart_service_booking.config.CloudinaryProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService implements CloudStorageService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;
    private final String uploadFolder; // inject from properties

    public CloudinaryService(Cloudinary cloudinary, CloudinaryProperties props) {
        this.cloudinary = cloudinary;
        this.uploadFolder = props == null || props.getUploadFolder() == null ? "smart_service_booking" : props.getUploadFolder();
    }

    public Map<String, Object> upload(MultipartFile file, Map<String, Object> options) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("folder", uploadFolder);
        if (options != null) uploadOptions.putAll(options);

        // Log basic info (do NOT log file bytes)
        log.debug("Uploading file: name={}, size={} bytes, options={}", file.getOriginalFilename(), file.getSize(), uploadOptions);

        // cloudinary.uploader().upload returns a raw Map; copy entries into a typed Map<String,Object>
        @SuppressWarnings("rawtypes")
        Map raw = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
        Map<String, Object> result = new HashMap<>();
        for (Object k : raw.keySet()) {
            result.put(k == null ? "null" : String.valueOf(k), raw.get(k));
        }
        return result;
    }

    public Map<String, Object> destroy(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("publicId must not be empty");
        }
        @SuppressWarnings("rawtypes")
        Map raw = cloudinary.uploader().destroy(publicId, new HashMap<>());
        Map<String, Object> result = new HashMap<>();
        for (Object k : raw.keySet()) {
            result.put(k == null ? "null" : String.valueOf(k), raw.get(k));
        }
        return result;
    }
}