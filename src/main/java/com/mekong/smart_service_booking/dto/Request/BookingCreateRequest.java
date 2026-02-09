package com.mekong.smart_service_booking.dto.Request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


@Data

public class BookingCreateRequest {

    private UUID customerId;
    private UUID serviceId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    
}
