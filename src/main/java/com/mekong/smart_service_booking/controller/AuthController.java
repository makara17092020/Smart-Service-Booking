package com.mekong.smart_service_booking.controller;

import com.mekong.smart_service_booking.dto.Request.RegisterRequest;
import com.mekong.smart_service_booking.dto.Response.RegisterResponse;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Automatically injects authService
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<RegisterResponse> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        User savedUser = authService.registerCustomer(
            request.getFullName(), 
            request.getEmail(), 
            request.getPassword()
        );

        return ResponseEntity.ok(new RegisterResponse(
            savedUser.getId(), 
            savedUser.getFullName(), 
            savedUser.getEmail(), 
            savedUser.getRole(), 
            "Registration successful"
        ));
    }
}