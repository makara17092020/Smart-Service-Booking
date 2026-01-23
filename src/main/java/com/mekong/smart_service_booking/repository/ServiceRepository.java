package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    // Helper: Find all services belonging to a specific category
    List<ServiceEntity> findByCategoryId(Long categoryId);

    // Helper: Find all services offered by a specific provider
    List<ServiceEntity> findByProviderId(UUID providerId);
}