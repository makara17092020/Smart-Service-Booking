
package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}