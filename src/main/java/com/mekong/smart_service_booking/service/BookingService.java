package com.mekong.smart_service_booking.service;

import com.mekong.smart_service_booking.dto.Request.BookingCreateRequest;
import com.mekong.smart_service_booking.dto.Response.BookingResponse;
import com.mekong.smart_service_booking.entity.Booking;
import com.mekong.smart_service_booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    // CUSTOMER: Create booking
    public BookingResponse createBooking(UUID customerId, BookingCreateRequest request) {

        boolean conflict =
                bookingRepository.existsByServiceIdAndBookingDateAndStartTimeLessThanAndEndTimeGreaterThan(
                        request.getServiceId(),
                        request.getBookingDate(),
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (conflict) {
            throw new RuntimeException("Booking time already taken");
        }

        Booking booking = Booking.builder()
                .customerId(customerId)
                .serviceId(request.getServiceId())
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    // CUSTOMER: View own bookings
    public List<BookingResponse> getBookingsByCustomer(UUID customerId) {
        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // PROVIDER: View bookings by service
    public List<BookingResponse> getBookingsByService(UUID serviceId) {
        return bookingRepository.findByServiceId(serviceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // PROVIDER / ADMIN: Update status
    public BookingResponse updateBookingStatus(UUID bookingId, String status) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return mapToResponse(bookingRepository.save(booking));
    }

    // ADMIN: View all bookings
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomerId())
                .serviceId(booking.getServiceId())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
