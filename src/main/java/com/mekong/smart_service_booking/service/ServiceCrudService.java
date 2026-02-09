package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.entity.Category;
import com.mekong.smart_service_booking.entity.ServiceEntity;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.CategoryRepository;
import com.mekong.smart_service_booking.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceCrudService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
    }

    @Transactional
    public ServiceEntity createService(ServiceEntity service) {
        User user = currentUserService.getCurrentUser();
        if (user == null) throw new RuntimeException("Unauthorized");

        // Sync redundant columns for DB compatibility
        if (service.getName() != null && service.getTitle() == null) {
            service.setTitle(service.getName());
        }
        
        // Sync both active status columns
        boolean status = service.getIsActive() != null ? service.getIsActive() : true;
        service.setIsActive(status);
        service.setActive(status);

        if (service.getDurationMinutes() == null) throw new RuntimeException("Duration is required");

        Category cat = categoryRepository.findById(service.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        service.setCategory(cat);
        service.setProvider(user);
        
        return serviceRepository.save(service);
    }

    @Transactional
    public ServiceEntity updateService(UUID id, ServiceEntity updatedData) {
        ServiceEntity existing = getServiceById(id);
        User user = currentUserService.getCurrentUser();

        if (!currentUserService.hasRole("ADMIN") && !existing.getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }

        existing.setName(updatedData.getName());
        existing.setTitle(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());
        existing.setDurationMinutes(updatedData.getDurationMinutes());
        
        if (updatedData.getIsActive() != null) {
            existing.setIsActive(updatedData.getIsActive());
            existing.setActive(updatedData.getIsActive());
        }

        return serviceRepository.save(existing);
    }

    @Transactional
    public void deleteService(UUID id) {
        ServiceEntity existing = getServiceById(id);
        User user = currentUserService.getCurrentUser();
        if (!currentUserService.hasRole("ADMIN") && !existing.getProvider().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden");
        }
        serviceRepository.delete(existing);
    }
}