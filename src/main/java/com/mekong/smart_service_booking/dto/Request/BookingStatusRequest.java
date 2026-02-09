package com.mekong.smart_service_booking.dto.Request;

import lombok.*;

@Data

public class BookingStatusRequest {

    private String status; //APPROVED, REJECTED, CANCELED
    
}
