package com.mekong.smart_service_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; 

    // Some existing databases expect an 'enabled' column (NOT NULL).
    // Default to true for newly registered users so INSERT doesn't fail when the DB has a NOT NULL constraint.
    @Column(nullable = false)
    private Boolean enabled = true;
}