
package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
}