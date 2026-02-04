package com.mekong.smart_service_booking.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat; // Add this line
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AvailabilityRequestDto {

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Availability date cannot be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate availableDate;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
}