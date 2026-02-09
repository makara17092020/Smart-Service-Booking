package com.mekong.smart_service_booking.service.impl;

import com.mekong.smart_service_booking.dto.Request.AvailabilityRequestDto;
import com.mekong.smart_service_booking.dto.Response.AvailabilityResponseDto;
import com.mekong.smart_service_booking.entity.Availability;
import com.mekong.smart_service_booking.entity.User;
import com.mekong.smart_service_booking.repository.AvailabilityRepository;
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

    @Override
    @Transactional
    public AvailabilityResponseDto createAvailability(AvailabilityRequestDto dto) {
        User currentUser = currentUserService.getCurrentUser();
        validateTimeSlot(dto);
        
        if (availabilityRepository.existsOverlappingSlot(
                currentUser.getId(), dto.getAvailableDate(), 
                dto.getStartTime(), dto.getEndTime(), null)) {
            throw new IllegalArgumentException("This time slot overlaps with an existing availability.");
        }

        Availability availability = Availability.builder()
                .provider(currentUser)
                .availableDate(dto.getAvailableDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        return mapToDto(availabilityRepository.save(availability));
    }

    @Override
    @Transactional
    public AvailabilityResponseDto updateAvailability(UUID id, AvailabilityRequestDto dto) {
        // Use FetchProvider here too so we can verify the owner
        Availability availability = availabilityRepository.findByIdFetchProvider(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        User currentUser = currentUserService.getCurrentUser();
        if (!availability.getProvider().getId().equals(currentUser.getId())) {
             throw new AccessDeniedException("You are not authorized to update this slot");
        }

        validateTimeSlot(dto);

        if (availabilityRepository.existsOverlappingSlot(
                currentUser.getId(), dto.getAvailableDate(), 
                dto.getStartTime(), dto.getEndTime(), id)) {
            throw new IllegalArgumentException("This time slot overlaps with an existing availability.");
        }

        availability.setAvailableDate(dto.getAvailableDate());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());

        return mapToDto(availabilityRepository.save(availability));
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponseDto getAvailabilityById(UUID id) {
        // FIX: Use findByIdFetchProvider to prevent LazyInitializationException
        Availability availability = availabilityRepository.findByIdFetchProvider(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        return mapToDto(availability);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getAllByProvider(UUID providerId) {
        return availabilityRepository.findAllByProviderId(providerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> getByProviderAndDate(UUID providerId, LocalDate date) {
        return availabilityRepository.findAllByProviderIdAndAvailableDate(providerId, date).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        User currentUser = currentUserService.getCurrentUser();
        if (!availability.getProvider().getId().equals(currentUser.getId())) {
             throw new AccessDeniedException("Forbidden: Unauthorized deletion");
        }
        availabilityRepository.delete(availability);
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