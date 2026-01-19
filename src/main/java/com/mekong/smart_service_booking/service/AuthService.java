package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.dto.Response.LoginResponse;
import com.mekong.smart_service_booking.dto.Response.RegisterResponse;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.entity.Role;
import com.mekong.smart_service_booking.repository.UserRepository;
import com.mekong.smart_service_booking.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

    public RegisterResponse registerCustomer(String fullName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email " + email + " is already taken");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        // assign CUSTOMER role
        Role customerRole = roleRepository.findByName("CUSTOMER").orElseGet(() -> {
            Role r = new Role();
            r.setName("CUSTOMER");
            r.setDescription("Default customer role");
            return roleRepository.save(r);
        });
        user.getRoles().add(customerRole);

        User saved = userRepository.save(user);

        // Build a Spring Security UserDetails to sign the token
        java.util.List<SimpleGrantedAuthority> authorities = saved.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
            .toList();

        UserDetails principal = new org.springframework.security.core.userdetails.User(
            saved.getEmail(),
            saved.getPassword(),
            authorities
        );

        String token = jwtService.generateToken(principal);

        return new RegisterResponse(
            saved.getId(),
            saved.getFullName(),
            saved.getEmail(),
            saved.getRoles().stream().map(Role::getName).toList(),
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
        java.util.List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
            .toList();

        UserDetails principal = new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            authorities
        );

        String token = jwtService.generateToken(principal);

        return new LoginResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRoles().stream().map(Role::getName).toList(),
            "Login successful",
            token
        );
    }
}