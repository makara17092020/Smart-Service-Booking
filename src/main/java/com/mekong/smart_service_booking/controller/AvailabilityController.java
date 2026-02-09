package com.mekong.smart_service_booking.controller;

import com.mekong.smart_service_booking.dto.Request.AvailabilityRequestDto;
import com.mekong.smart_service_booking.dto.Response.AvailabilityResponseDto;
import com.mekong.smart_service_booking.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
    public AvailabilityResponseDto create(@RequestBody AvailabilityRequestDto dto) {
        return availabilityService.createAvailability(dto);
    }

    @GetMapping("/{id}")
    public AvailabilityResponseDto getById(@PathVariable UUID id) {
        return availabilityService.getAvailabilityById(id);
    }

    @GetMapping("/provider/{providerId}")
    public List<AvailabilityResponseDto> getByProvider(@PathVariable UUID providerId) {
        return availabilityService.getAllByProvider(providerId);
    }

    @GetMapping("/provider/{providerId}/filter")
    public List<AvailabilityResponseDto> getByDate(
            @PathVariable UUID providerId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.getByProviderAndDate(providerId, date);
    }

    @PutMapping("/{id}")
    public AvailabilityResponseDto update(@PathVariable UUID id, @RequestBody AvailabilityRequestDto dto) {
        return availabilityService.updateAvailability(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}