package com.mekong.smart_service_booking.repository;

import com.mekong.smart_service_booking.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    /**
     * Fixes LazyInitializationException for GetById
     */
    @Query("SELECT a FROM Availability a JOIN FETCH a.provider WHERE a.id = :id")
    Optional<Availability> findByIdFetchProvider(@Param("id") UUID id);

    @Query("SELECT a FROM Availability a JOIN FETCH a.provider WHERE a.provider.id = :providerId")
    List<Availability> findAllByProviderId(@Param("providerId") UUID providerId);

    @Query("SELECT a FROM Availability a JOIN FETCH a.provider " +
           "WHERE a.provider.id = :providerId AND a.availableDate = :availableDate")
    List<Availability> findAllByProviderIdAndAvailableDate(@Param("providerId") UUID providerId, 
                                                           @Param("availableDate") LocalDate availableDate);

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