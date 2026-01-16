package com.mekong.smart_service_booking.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Abstraction for file storage providers (Cloudinary, local, S3, etc.).
 */
public interface CloudStorageService {
    Map<String, Object> upload(MultipartFile file, Map<String, Object> options) throws IOException;

    Map<String, Object> destroy(String publicId) throws IOException;
}
