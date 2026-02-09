package com.mekong.smart_service_booking.dto.Response;

import lombok.Builder;
import lombok.Data;

import java.time.*;
import java.util.UUID;


@Data
@Builder

public class BookingResponse {

    private UUID id;
    private UUID customerId;
    private UUID serviceId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private LocalDateTime createdAt;
    
}
