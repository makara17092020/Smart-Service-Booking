package com.mekong.smart_service_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Double price;

    private String duration; // e.g., "2 hours"

    // RELATIONS

    // Link to Category (Which uses Long ID)
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Link to Provider (User)
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;
}