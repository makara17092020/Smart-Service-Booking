package com.mekong.smart_service_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;  // e.g., CUSTOMER, PROVIDER, ADMIN

    private String description;

    // Explicitly adding the getter to solve the "cannot find symbol getName()" error
    public String getName() {
        return this.name;
    }
}