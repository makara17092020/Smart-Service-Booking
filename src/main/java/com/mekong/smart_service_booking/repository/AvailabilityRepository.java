package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    /**
     * Finds all availability slots for a specific provider.
     * Uses JOIN FETCH to load the provider entity immediately to avoid LazyInitializationException.
     */
    @Query("SELECT a FROM Availability a JOIN FETCH a.provider WHERE a.provider.id = :providerId")
    List<Availability> findAllByProviderId(@Param("providerId") UUID providerId);

    /**
     * Finds all availability slots for a specific provider on a specific date.
     * Uses JOIN FETCH to ensure provider details (like fullName) are accessible in the Service layer.
     */
    @Query("SELECT a FROM Availability a JOIN FETCH a.provider " +
           "WHERE a.provider.id = :providerId AND a.availableDate = :availableDate")
    List<Availability> findAllByProviderIdAndAvailableDate(@Param("providerId") UUID providerId, 
                                                           @Param("availableDate") LocalDate availableDate);

    /**
     * Logic: (StartA < EndB) and (EndA > StartB) checks for any overlap.
     * This query does not need JOIN FETCH because it only returns a boolean.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Availability a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.availableDate = :date " +
           "AND a.startTime < :endTime " +
           "AND a.endTime > :startTime " +
           "AND (:excludeId IS NULL OR a.id != :excludeId)")
    boolean existsOverlappingSlot(@Param("providerId") UUID providerId,
                                  @Param("date") LocalDate date,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime,
                                  @Param("excludeId") UUID excludeId);
}