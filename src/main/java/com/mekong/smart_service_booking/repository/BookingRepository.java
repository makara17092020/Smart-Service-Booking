package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByCustomerId(UUID customerId);
    List<Booking> findByServiceId(UUID serviceId);

    boolean existsByServiceIdAndBookingDateAndStartTimeLessThanAndEndTimeGreaterThan(
        UUID serviceId,
        LocalDate bookingDate,
        LocalTime endTime,
        LocalTime startTime     

    );
    
}
