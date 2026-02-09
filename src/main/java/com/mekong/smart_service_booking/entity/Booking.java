package com.mekong.smart_service_booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
     private UUID serviceId;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    
}
