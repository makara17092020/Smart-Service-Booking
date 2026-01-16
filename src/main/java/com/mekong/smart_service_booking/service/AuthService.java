package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerCustomer(String fullName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email " + email + " is already taken");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("CUSTOMER");

        return userRepository.save(user);
    }
}