package com.mekong.smart_service_booking.config;

import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.UserRepository;
import com.mekong.smart_service_booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Component
@Profile("local") // only seed when running with the 'local' profile
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void run(String... args) {
        log.info("Running DataInitializer (profile=local) — seeding test users if missing");

        List<SeedUser> seeds = Arrays.asList(
            new SeedUser("admin@local.test", "Admin User", "Admin123!", "ADMIN"),
            new SeedUser("provider@local.test", "Provider User", "Provider123!", "PROVIDER"),
            new SeedUser("customer@local.test", "Customer User", "Customer123!", "CUSTOMER")
        );

        for (SeedUser s : seeds) {
            if (userRepository.existsByEmail(s.email)) {
                log.info("User already exists: {} (role={}) — skipping", s.email, s.role);
                continue;
            }

            User u = new User();
            u.setFullName(s.fullName);
            u.setEmail(s.email);
            u.setPassword(passwordEncoder.encode(s.password));
            u.setRole(s.role);
            u.setEnabled(true);

            User saved = userRepository.save(u);

            // Build UserDetails and generate JWT
            org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                    saved.getEmail(),
                    saved.getPassword(),
                    Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + saved.getRole()))
                );

            String token = jwtService.generateToken(principal);

            log.info("Seeded user: {} (role={})", saved.getEmail(), saved.getRole());
            log.info("  password: {}", s.password);
            log.info("  token: {}", token);
            log.info("  Use in Swagger Authorize as: Bearer {}", token);
        }

        log.info("DataInitializer complete");
    }

    private static class SeedUser {
        final String email;
        final String fullName;
        final String password;
        final String role;

        SeedUser(String email, String fullName, String password, String role) {
            this.email = email;
            this.fullName = fullName;
            this.password = password;
            this.role = role;
        }
    }
}
