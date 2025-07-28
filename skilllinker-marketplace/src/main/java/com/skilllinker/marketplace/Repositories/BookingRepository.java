package com.skilllinker.marketplace.Repositories;

import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = :status WHERE b.id = :id")
    void updateStatus(Long id, BookingStatus status);


    @Modifying
    @Transactional
    void deleteByStatus(BookingStatus status);


    @Modifying
    @Transactional
    void deleteByBookingDateBefore(LocalDateTime date);


    List<Booking> findByCustomerId(Long customerId);


    List<Booking> findByProfessionalId(Long professionalId);


    List<Booking> findByStatus(BookingStatus status);


    List<Booking> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);


    List<Booking> findByServiceId(Long serviceId);
}
