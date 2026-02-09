 
package com.mekong.smart_service_booking.controller;

import com.mekong.smart_service_booking.dto.Request.BookingCreateRequest;
import com.mekong.smart_service_booking.dto.Request.BookingStatusRequest;
import com.mekong.smart_service_booking.dto.Response.BookingResponse;
import com.mekong.smart_service_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // CUSTOMER
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public BookingResponse createBooking(
            @RequestParam UUID customerId,
            @RequestBody BookingCreateRequest request
    ) {
        return bookingService.createBooking(customerId, request);
    }

    // CUSTOMER
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/{customerId}")
    public List<BookingResponse> getCustomerBookings(@PathVariable UUID customerId) {
        return bookingService.getBookingsByCustomer(customerId);
    }

    // PROVIDER
    @PreAuthorize("hasRole('PROVIDER')")
    @GetMapping("/service/{serviceId}")
    public List<BookingResponse> getServiceBookings(@PathVariable UUID serviceId) {
        return bookingService.getBookingsByService(serviceId);
    }

    // PROVIDER / ADMIN
    @PreAuthorize("hasAnyRole('PROVIDER','ADMIN')")
    @PutMapping("/{bookingId}/status")
    public BookingResponse updateStatus(
            @PathVariable UUID bookingId,
            @RequestBody BookingStatusRequest request
    ) {
        return bookingService.updateBookingStatus(bookingId, request.getStatus());
    }

    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
