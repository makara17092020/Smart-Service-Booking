package com.mekong.smart_service_booking.dto.Response;

import java.util.Map;

public record ErrorResponse(
    int status,
    String message,
    long timestamp,
    Map<String, String> errors // Used for validation field errors
) {}