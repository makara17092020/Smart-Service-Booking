package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.entity.Category;
import com.mekong.smart_service_booking.entity.ServiceEntity;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.CategoryRepository;
import com.mekong.smart_service_booking.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceCrudService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;
    private final CloudStorageService cloudStorageService; // Uses your interface

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Transactional
    public ServiceEntity createService(ServiceEntity service, MultipartFile imageFile) throws IOException {
        User user = currentUserService.getCurrentUser();
        if (user == null) throw new RuntimeException("Unauthorized: Please log in.");

        // Security Check
        if (!currentUserService.hasRole("ADMIN") && !currentUserService.hasRole("PROVIDER")) {
            throw new RuntimeException("Access Denied: Only Providers or Admins can create services.");
        }

        // Database Column Syncing
        service.setTitle(service.getName());
        boolean status = service.getIsActive() != null ? service.getIsActive() : true;
        service.setIsActive(status);
        service.setActive(status);

        // Handle Image Upload using your CloudinaryService
        if (imageFile != null && !imageFile.isEmpty()) {
            Map<String, Object> uploadResult = cloudStorageService.upload(imageFile, new HashMap<>());
            // Cloudinary returns the URL in "secure_url" or "url"
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", uploadResult.get("url"));
            service.setImageUrl(imageUrl);
        }

        Category cat = categoryRepository.findById(service.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        service.setCategory(cat);
        service.setProvider(user);
        
        return serviceRepository.save(service);
    }

    @Transactional
    public ServiceEntity updateService(UUID id, ServiceEntity updatedData, MultipartFile imageFile) throws IOException {
        ServiceEntity existing = getServiceById(id);
        User user = currentUserService.getCurrentUser();

        if (!currentUserService.hasRole("ADMIN") && !existing.getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: You do not own this service.");
        }

        existing.setName(updatedData.getName());
        existing.setTitle(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());
        existing.setDurationMinutes(updatedData.getDurationMinutes());

        if (imageFile != null && !imageFile.isEmpty()) {
            Map<String, Object> uploadResult = cloudStorageService.upload(imageFile, new HashMap<>());
            String imageUrl = (String) uploadResult.getOrDefault("secure_url", uploadResult.get("url"));
            existing.setImageUrl(imageUrl);
        }

        return serviceRepository.save(existing);
    }

    @Transactional
    public void deleteService(UUID id) {
        ServiceEntity existing = getServiceById(id);
        serviceRepository.delete(existing);
    }
}