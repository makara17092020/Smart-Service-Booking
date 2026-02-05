package com.mekong.smart_service_booking.controller;

import com.mekong.smart_service_booking.dto.Request.AvailabilityRequestDto;
import com.mekong.smart_service_booking.dto.Response.AvailabilityResponseDto;
import com.mekong.smart_service_booking.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<AvailabilityResponseDto> createAvailability(@Valid @RequestBody AvailabilityRequestDto dto) {
        return new ResponseEntity<>(availabilityService.createAvailability(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<AvailabilityResponseDto> updateAvailability(
            @PathVariable UUID id,
            @Valid @RequestBody AvailabilityRequestDto dto) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDto> getAvailabilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AvailabilityResponseDto>> getProviderAvailability(@PathVariable UUID providerId) {
        return ResponseEntity.ok(availabilityService.getAllByProvider(providerId));
    }

    @GetMapping("/provider/{providerId}/date/{date}")
    public ResponseEntity<List<AvailabilityResponseDto>> getProviderAvailabilityByDate(
            @PathVariable UUID providerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getByProviderAndDate(providerId, date));
    }
}