package com.mekong.smart_service_booking.dto.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class AvailabilityResponseDto {
    private UUID id;
    private UUID providerId;
    private String providerName; // Useful for frontend display
    private LocalDate availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean booked;
}