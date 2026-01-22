package com.mekong.smart_service_booking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID; // Import UUID

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Changed to AUTO for UUID generation
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id; // Changed from Long to UUID

    @Column(nullable = false)
    private String name;

    private String description;
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;
}
