package com.mekong.smart_service_booking.dto.Response;

import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private UUID id;
    private String fullName;
    private String email;
    private java.util.List<String> roles;
    private String message;
    private String token;
}
