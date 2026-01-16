package com.mekong.smart_service_booking.dto.Response;

import lombok.*;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private Map<String, String> errors;
}