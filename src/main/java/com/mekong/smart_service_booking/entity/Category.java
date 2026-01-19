package com.mekong.smart_service_booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @JsonIgnore // Fixes the 500 error by preventing infinite loops and lazy loading crashes
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ServiceEntity> services;
}