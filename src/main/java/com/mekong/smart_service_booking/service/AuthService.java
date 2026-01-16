package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.dto.Response.LoginResponse;
import com.mekong.smart_service_booking.dto.Response.RegisterResponse;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.UserRepository;
import com.mekong.smart_service_booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegisterResponse registerCustomer(String fullName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email " + email + " is already taken");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("CUSTOMER");

        User saved = userRepository.save(user);

        // Build a Spring Security UserDetails to sign the token
        UserDetails principal = new org.springframework.security.core.userdetails.User(
            saved.getEmail(),
            saved.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + saved.getRole()))
        );

        String token = jwtService.generateToken(principal);

        return new RegisterResponse(
            saved.getId(),
            saved.getFullName(),
            saved.getEmail(),
            saved.getRole(),
            "Registration successful",
            token
        );
    }

    public LoginResponse loginUser(String email, String password) {
        // Find user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Invalid email or password");
        }

        User user = optionalUser.get();

        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("Invalid email or password");
        }

        // Check if user is enabled
        if (!user.getEnabled()) {
            throw new IllegalStateException("User account is disabled");
        }

        // Build a Spring Security UserDetails to sign the token
        UserDetails principal = new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

        String token = jwtService.generateToken(principal);

        return new LoginResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            "Login successful",
            token
        );
    }
}