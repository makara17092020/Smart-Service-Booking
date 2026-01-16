package com.mekong.smart_service_booking.dto.Request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data // Generates getters, setters, and toString
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}