package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.dto.Request.AvailabilityRequestDto;
import com.mekong.smart_service_booking.dto.Response.AvailabilityResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {
    AvailabilityResponseDto createAvailability(AvailabilityRequestDto dto);
    AvailabilityResponseDto updateAvailability(UUID id, AvailabilityRequestDto dto);
    void deleteAvailability(UUID id);
    AvailabilityResponseDto getAvailabilityById(UUID id);
    List<AvailabilityResponseDto> getAllByProvider(UUID providerId);
    List<AvailabilityResponseDto> getByProviderAndDate(UUID providerId, LocalDate date);
}