package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // This is the missing method causing your red squiggly lines
    Optional<User> findByEmail(String email);
}