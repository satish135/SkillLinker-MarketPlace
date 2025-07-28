package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.BookingRequest;
import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.Enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse updateBookingStatus(Long id, BookingStatus status);

    void deleteBooking(Long id);

    void deleteByStatus(BookingStatus status);

    void deleteExpiredBookings(LocalDateTime date);

    BookingResponse getBookingById(Long id);

    List<BookingResponse> findByCustomerId(Long customerId);

    List<BookingResponse> findByProfessionalId(Long professionalId);

    List<BookingResponse> findByStatus(BookingStatus status);

    List<BookingResponse> findByBookingDateBetween(LocalDateTime start, LocalDateTime end);

    List<BookingResponse> findByServiceId(Long serviceId);

    Page<BookingResponse> findAll(Pageable pageable);
}
