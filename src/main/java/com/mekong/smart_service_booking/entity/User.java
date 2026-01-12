
package com.mekong.smart_service_booking.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    private String email;

    private String password;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_approved")
    private boolean isApproved = false;  // Default to false for providers

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}