package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Add custom queries if needed, e.g., findByEmail
}