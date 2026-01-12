
package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
}