package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    /**
     * Retrieves the currently logged-in User entity from the database
     * based on the JWT token.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is actually logged in
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        // The "Name" in authentication is usually the email (from UserDetails)
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Checks if the current user has a specific role (e.g., "ADMIN").
     * Handles the "ROLE_" prefix automatically.
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Check authorities (e.g., matches "ADMIN" or "ROLE_ADMIN")
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role) ||
                        a.getAuthority().equals("ROLE_" + role));
    }
}