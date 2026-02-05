package com.mekong.smart_service_booking.config;

import com.mekong.smart_service_booking.security.JwtAuthenticationFilter;
import com.mekong.smart_service_booking.security.JwtAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
            // 1. Enable CORS using the bean defined below
            .cors(Customizer.withDefaults())
            // 2. Disable CSRF (standard for Stateless APIs)
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler())
            )
            .authorizeHttpRequests(auth -> auth
                // Allow Preflight OPTIONS requests for all paths
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Public Auth endpoints
                .requestMatchers("/api/auth/**").permitAll()
                
                // Swagger Documentation
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Categories Access Logic
                // If you want categories to be visible BEFORE login, change hasAnyRole to permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").hasAnyRole("ADMIN", "CUSTOMER", "PROVIDER")
                .requestMatchers("/api/categories/**").hasRole("ADMIN")

                // Admin specific paths
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // All other requests require a valid JWT
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3. CORS Configuration Source Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ALLOWED ORIGINS: Add your local and production frontend URLs here
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "http://localhost:5173", 
            "https://smart-service-booking-develop.onrender.com" // If your frontend is also here
        ));
        
        // ALLOWED METHODS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // ALLOWED HEADERS
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // Allow cookies/auth headers
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            String jsonResponse = "{"
                + "\"status\": 403,"
                + "\"message\": \"Access Denied: You do not have permission to perform this action.\","
                + "\"timestamp\": " + System.currentTimeMillis()
                + "}";
            response.getWriter().write(jsonResponse);
        };
    }
}