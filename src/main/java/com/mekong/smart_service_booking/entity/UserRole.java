package com.mekong.smart_service_booking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@Data
@NoArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // Explicitly add these if compiler fails to find Lombok symbols
    public Role getRole() { return this.role; }
    public User getUser() { return this.user; }
}