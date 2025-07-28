package com.skilllinker.marketplace.Controller;

import com.skilllinker.marketplace.DTO.BookingRequest;
import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.StatusUpdateRequest;
import com.skilllinker.marketplace.Enums.BookingStatus;
import com.skilllinker.marketplace.Service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        BookingResponse response = bookingService.updateBookingStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByStatus(@PathVariable BookingStatus status) {
        bookingService.deleteByStatus(status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExpiredBookings(@RequestParam LocalDateTime date) {
        bookingService.deleteExpiredBookings(date);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<BookingResponse>> findByCustomerId(@PathVariable Long customerId) {
        List<BookingResponse> responses = bookingService.findByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/professional/{professionalId}")
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ResponseEntity<List<BookingResponse>> findByProfessionalId(@PathVariable Long professionalId) {
        List<BookingResponse> responses = bookingService.findByProfessionalId(professionalId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookingResponse>> findByStatus(@PathVariable BookingStatus status) {
        List<BookingResponse> responses = bookingService.findByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookingResponse>> findByBookingDateBetween(
            @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        List<BookingResponse> responses = bookingService.findByBookingDateBetween(start, end);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/service/{serviceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookingResponse>> findByServiceId(@PathVariable Long serviceId) {
        List<BookingResponse> responses = bookingService.findByServiceId(serviceId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponse>> findAll(Pageable pageable) {
        Page<BookingResponse> page = bookingService.findAll(pageable);
        return ResponseEntity.ok(page);
    }
}
