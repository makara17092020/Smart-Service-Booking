package com.mekong.smart_service_booking.dto.Response;

import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String role;
    private String message;
}