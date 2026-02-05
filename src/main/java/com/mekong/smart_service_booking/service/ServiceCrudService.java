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

    // 1. READ ALL
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    // 2. READ ONE
    public ServiceEntity getServiceById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
    }

    // 3. CREATE
    @Transactional
    public ServiceEntity createService(ServiceEntity service) {
        User user = currentUserService.getCurrentUser();
        if (user == null) throw new RuntimeException("Unauthorized: Please log in.");

        // Check Role
        if (!currentUserService.hasRole("ADMIN") && !currentUserService.hasRole("PROVIDER")) {
            throw new RuntimeException("Access Denied: Only Providers or Admins can create services.");
        }

        // Validate Category ID (Long)
        if (service.getCategory() == null || service.getCategory().getId() == null) {
            throw new RuntimeException("Category ID is required!");
        }

        Category cat = categoryRepository.findById(service.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        service.setCategory(cat);

        service.setProvider(user);
        return serviceRepository.save(service);
    }

    // 4. UPDATE
    @Transactional
    public ServiceEntity updateService(UUID id, ServiceEntity updatedData) {
        ServiceEntity existing = getServiceById(id);
        User user = currentUserService.getCurrentUser();

        // Check Ownership
        if (!currentUserService.hasRole("ADMIN")) {
            if (!existing.getProvider().getId().equals(user.getId())) {
                throw new RuntimeException("Forbidden: You do not own this service.");
            }
        }

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.setPrice(updatedData.getPrice());
        existing.setDuration(updatedData.getDuration());

        return serviceRepository.save(existing);
    }

    // 5. DELETE
    @Transactional
    public void deleteService(UUID id) {
        ServiceEntity existing = getServiceById(id);
        User user = currentUserService.getCurrentUser();

        if (!currentUserService.hasRole("ADMIN")) {
            if (!existing.getProvider().getId().equals(user.getId())) {
                throw new RuntimeException("Forbidden: You do not own this service.");
            }
        }
        serviceRepository.delete(existing);
    }
}