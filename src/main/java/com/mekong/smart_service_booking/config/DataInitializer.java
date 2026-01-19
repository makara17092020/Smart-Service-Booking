package com.mekong.smart_service_booking.config;

import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.entity.Role;
import com.mekong.smart_service_booking.repository.UserRepository;
import com.mekong.smart_service_booking.repository.RoleRepository;
import com.mekong.smart_service_booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Component
@Profile({"local","default"}) // run when profile is 'local' or no profile (default)
@Transactional
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void run(String... args) {
        log.info("Running DataInitializer (profile=local) — seeding test users if missing");

        List<SeedUser> seeds = Arrays.asList(
            new SeedUser("admin@local.test", "Admin User", "Admin123!", java.util.List.of("ADMIN")),
            new SeedUser("provider@local.test", "Provider User", "Provider123!", java.util.List.of("PROVIDER")),
            new SeedUser("customer@local.test", "Customer User", "Customer123!", java.util.List.of("CUSTOMER"))
        );

        for (SeedUser s : seeds) {
            if (userRepository.existsByEmail(s.email)) {
                log.info("User already exists: {} (roles={}) — skipping", s.email, s.roles);
                continue;
            }

            User u = new User();
            u.setFullName(s.fullName);
            u.setEmail(s.email);
            u.setPassword(passwordEncoder.encode(s.password));
            u.setEnabled(true);

            // ensure roles exist and attach
            for (String roleName : s.roles) {
                Role role = roleRepository.findByName(roleName).orElseGet(() -> {
                    Role r = new Role();
                    r.setName(roleName);
                    r.setDescription(roleName + " role");
                    return roleRepository.save(r);
                });
                u.getRoles().add(role);
            }

            User saved = userRepository.save(u);

            // Build UserDetails and generate JWT
            java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities =
                saved.getRoles().stream()
                    .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.getName()))
                    .toList();

            org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                    saved.getEmail(),
                    saved.getPassword(),
                    authorities
                );

            String token = jwtService.generateToken(principal);

            log.info("Seeded user: {} (roles={})", saved.getEmail(), saved.getRoles().stream().map(Role::getName).toList());
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
        final java.util.List<String> roles;

        SeedUser(String email, String fullName, String password, java.util.List<String> roles) {
            this.email = email;
            this.fullName = fullName;
            this.password = password;
            this.roles = roles;
        }
    }
}
