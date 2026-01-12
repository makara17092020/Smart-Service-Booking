
package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
}