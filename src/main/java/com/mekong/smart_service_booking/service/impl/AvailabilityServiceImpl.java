package com.mekong.smart_service_booking.service.impl;

import com.mekong.smart_service_booking.dto.Request.AvailabilityRequestDto;
import com.mekong.smart_service_booking.dto.Response.AvailabilityResponseDto;
import com.mekong.smart_service_booking.entity.Availability;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.AvailabilityRepository;
// Remove unused import: com.mekong.smart_service_booking.repository.UserRepository;
import com.mekong.smart_service_booking.service.AvailabilityService;
import com.mekong.smart_service_booking.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final CurrentUserService currentUserService;
    // FIX: Removed unused 'private final UserRepository userRepository;'

    @Override
    @Transactional
    public AvailabilityResponseDto createAvailability(AvailabilityRequestDto dto) {
        User currentUser = currentUserService.getCurrentUser();
        
        validateTimeSlot(dto);
        
        if (availabilityRepository.existsOverlappingSlot(
                currentUser.getId(), 
                dto.getAvailableDate(), 
                dto.getStartTime(), 
                dto.getEndTime(), 
                null)) {
            throw new IllegalArgumentException("This time slot overlaps with an existing availability.");
        }

        Availability availability = Availability.builder()
                .provider(currentUser)
                .availableDate(dto.getAvailableDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                // .isBooked(false) is handled by @Builder.Default now
                .build();

        Availability saved = availabilityRepository.save(availability);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public AvailabilityResponseDto updateAvailability(UUID id, AvailabilityRequestDto dto) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        User currentUser = currentUserService.getCurrentUser();
        
        if (!availability.getProvider().getId().equals(currentUser.getId())) {
             throw new AccessDeniedException("You are not authorized to update this slot");
        }

        validateTimeSlot(dto);

        if (availabilityRepository.existsOverlappingSlot(
                currentUser.getId(), 
                dto.getAvailableDate(), 
                dto.getStartTime(), 
                dto.getEndTime(), 
                id)) {
            throw new IllegalArgumentException("This time slot overlaps with an existing availability.");
        }

        availability.setAvailableDate(dto.getAvailableDate());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());

        Availability updated = availabilityRepository.save(availability);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        User currentUser = currentUserService.getCurrentUser();
        
        if (!availability.getProvider().getId().equals(currentUser.getId())) {
             throw new AccessDeniedException("You are not authorized to delete this slot");
        }

        availabilityRepository.delete(availability);
    }

    @Override
    public AvailabilityResponseDto getAvailabilityById(UUID id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        return mapToDto(availability);
    }

    @Override
    public List<AvailabilityResponseDto> getAllByProvider(UUID providerId) {
        return availabilityRepository.findAllByProviderId(providerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailabilityResponseDto> getByProviderAndDate(UUID providerId, LocalDate date) {
        return availabilityRepository.findAllByProviderIdAndAvailableDate(providerId, date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private void validateTimeSlot(AvailabilityRequestDto dto) {
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    private AvailabilityResponseDto mapToDto(Availability availability) {
        return AvailabilityResponseDto.builder()
                .id(availability.getId())
                .providerId(availability.getProvider().getId())
                .providerName(availability.getProvider().getFullName())
                .availableDate(availability.getAvailableDate())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .booked(availability.isBooked())
                .build();
    }
}