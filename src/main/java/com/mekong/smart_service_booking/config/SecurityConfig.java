package com.mekong.smart_service_booking.config;

import com.mekong.smart_service_booking.security.JwtAuthenticationFilter;
import com.mekong.smart_service_booking.security.JwtAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, 
                          AuthenticationProvider authenticationProvider,
                          JwtAuthenticationEntryPoint jwtEntryPoint) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.jwtEntryPoint = jwtEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler()) // Connects the 403 fix
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // 1. PUBLIC READ: Anyone logged in can GET categories
                .requestMatchers(HttpMethod.GET, "/api/categories/**")
                    .hasAnyRole("ADMIN", "CUSTOMER", "PROVIDER")
                
                // 2. ADMIN POWER: Only Admin can POST, PUT, DELETE
                .requestMatchers("/api/categories/**").hasRole("ADMIN")

                // 3. OTHER PATHS
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            // The JSON message the Customer/Provider will see
            String jsonResponse = "{"
                + "\"status\": 403,"
                + "\"message\": \"Access Denied: Only Admins can modify categories. Customers and Providers are Read-Only.\","
                + "\"timestamp\": " + System.currentTimeMillis()
                + "}";
            response.getWriter().write(jsonResponse);
        };
    }
}