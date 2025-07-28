package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.Config.CustomUserDetails;
import com.skilllinker.marketplace.DTO.BookingRequest;
import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.ProfessionalProfileResponse;
import com.skilllinker.marketplace.DTO.ReviewResponse;
import com.skilllinker.marketplace.DTO.ServiceResponse;
import com.skilllinker.marketplace.DTO.TransactionResponse;
import com.skilllinker.marketplace.DTO.UserResponse;
import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Entity.Professional;
import com.skilllinker.marketplace.Entity.Review;
import com.skilllinker.marketplace.Entity.Service;
import com.skilllinker.marketplace.Entity.Transaction;
import com.skilllinker.marketplace.Entity.User;
import com.skilllinker.marketplace.Enums.BookingStatus;
import com.skilllinker.marketplace.Repositories.BookingRepository;
import com.skilllinker.marketplace.Repositories.ProfessionalRepository;
import com.skilllinker.marketplace.Repositories.ServiceRepository;
import com.skilllinker.marketplace.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public BookingResponse createBooking(BookingRequest request) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        User customer = userRepository.findById(isAdmin ? request.getCustomerId() : currentUserId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Professional professional = professionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Booking booking = new Booking();
        booking.setBookingDate(request.getBookingDate());
        booking.setStatus(request.getStatus());
        booking.setAddress(request.getAddress());
        booking.setPaymentStatus(request.getPaymentStatus());
        booking.setTotalFee(request.getTotalFee());
        booking.setCustomer(customer);
        booking.setProfessional(professional);
        booking.setService(service);
        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id " + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        boolean isProfessional = booking.getProfessional().getUser().getId().equals(currentUserId);

        if (!isAdmin && !isProfessional) {
            throw new RuntimeException("You can only update bookings for your own professionals");
        }

        booking.setStatus(status);
        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROFESSIONAL', 'ADMIN')")
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with Id : " + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        boolean isProfessional = booking.getProfessional().getUser().getId().equals(currentUserId);
        boolean isCustomer = booking.getCustomer().getId().equals(currentUserId);

        if (!isAdmin && !isProfessional && !isCustomer) {
            throw new RuntimeException("You can only delete your own bookings or as admin");
        }

        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByStatus(BookingStatus status) {
        bookingRepository.deleteByStatus(status);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteExpiredBookings(LocalDateTime date) {
        bookingRepository.deleteByBookingDateBefore(date);
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PROFESSIONAL', 'ADMIN')")
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        boolean isCustomer = booking.getCustomer().getId().equals(currentUserId);
        boolean isProfessional = booking.getProfessional().getUser().getId().equals(currentUserId);

        if (!isAdmin && !isProfessional && !isCustomer) {
            throw new RuntimeException("You can only view your own bookings");
        }

        return mapToResponse(booking);
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public List<BookingResponse> findByCustomerId(Long customerId) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        if (!isAdmin && !customerId.equals(currentUserId)) {
            throw new RuntimeException("You can only view your own bookings");
        }

        return bookingRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public List<BookingResponse> findByProfessionalId(Long professionalId) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        Professional professional = professionalRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professional not found"));

        if (!isAdmin && !professional.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only view bookings for your own professionals");
        }

        return bookingRepository.findByProfessionalId(professionalId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> findByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> findByBookingDateBetween(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByBookingDateBetween(start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> findByServiceId(Long serviceId) {
        return bookingRepository.findByServiceId(serviceId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<BookingResponse> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(this::mapToResponse);
    }


    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .professionalId(booking.getProfessional() != null ? booking.getProfessional().getId() : null)
                .serviceId(booking.getService() != null ? booking.getService().getId() : null)
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus() != null ? booking.getStatus().name() : null)
                .totalFee(booking.getTotalFee())
                .paymentStatus(booking.getPaymentStatus())
                .address(booking.getAddress())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();


        if (booking.getCustomer() != null) {
            User customer = booking.getCustomer();
            response.setCustomer(UserResponse.builder()
                    .id(customer.getId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmail())
                    .phoneNumber(customer.getPhoneNumber())
                    .role(customer.getRole())
                    .status(customer.getStatus())
                    .createdAt(customer.getCreatedAt())
                    .updatedAt(customer.getUpdatedAt())
                    .build());
        }


        if (booking.getProfessional() != null) {
            Professional professional = booking.getProfessional();
            response.setProfessional(ProfessionalProfileResponse.builder()
                    .id(professional.getId())
                    .categories(professional.getCategories())
                    .skills(professional.getSkills())
                    .experienceYear(professional.getExperienceYear())
                    .document(professional.getDocument())
                    .profileViews(professional.getProfileViews())
                    .activeStatus(professional.getActiveStatus())
                    .build());
        }


        if (booking.getService() != null) {
            Service service = booking.getService();
            response.setService(ServiceResponse.builder()
                    .id(service.getId())
                    .name(service.getName())
                    .description(service.getDescription())
                    .minBookingFee(service.getMinBookingFee())
                    .build());
        }


        if (booking.getTransaction() != null) {
            Transaction transaction = booking.getTransaction();
            response.setTransaction(TransactionResponse.builder()
                    .id(transaction.getId())
                    .bookingId(transaction.getBooking() != null ? transaction.getBooking().getId() : null)
                    .amount(transaction.getAmount())
                    .status(transaction.getStatus())
                    .paymentMethod(transaction.getPaymentMethod())
                    .createdAt(transaction.getCreatedAt())
                    .build());
        }


        if (booking.getReview() != null) {
            Review review = booking.getReview();
            response.setReview(ReviewResponse.builder()
                    .id(review.getId())
                    .bookingId(review.getBooking() != null ? review.getBooking().getId() : null)
                    .reviewerId(review.getReviewer() != null ? review.getReviewer().getId() : null)
                    .professionalId(review.getProfessional() != null ? review.getProfessional().getId() : null)
                    .rating(review.getRating())
                    .comment(review.getComment())
                    .createdAt(review.getCreatedAt())
                    .build());
        }

        return response;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
