package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.Config.CustomUserDetails;
import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.ProfessionalProfileResponse;
import com.skilllinker.marketplace.DTO.ReviewRequest;
import com.skilllinker.marketplace.DTO.ReviewResponse;
import com.skilllinker.marketplace.DTO.UserResponse;
import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Entity.Professional;
import com.skilllinker.marketplace.Entity.Review;
import com.skilllinker.marketplace.Entity.User;
import com.skilllinker.marketplace.Repositories.BookingRepository;
import com.skilllinker.marketplace.Repositories.ProfessionalRepository;
import com.skilllinker.marketplace.Repositories.ReviewRepository;
import com.skilllinker.marketplace.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponse createReview(ReviewRequest request) {
        Long currentUserId = getCurrentUserId();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + request.getBookingId()));


        if (!booking.getCustomer().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only review your own bookings");
        }


        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new RuntimeException("A review already exists for this booking");
        }

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new RuntimeException("Reviewer not found with ID: " + request.getReviewerId()));

        Professional professional = professionalRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Professional not found with ID: " + request.getProfessionalId()));

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setBooking(booking);
        review.setReviewer(reviewer);
        review.setProfessional(professional);
        review = reviewRepository.save(review);


        booking.setReview(review);
        bookingRepository.save(booking);

        return mapToResponse(review);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();


        if (!isAdmin && !review.getReviewer().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        return mapToResponse(review);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));


        if (review.getBooking() != null) {
            Booking booking = review.getBooking();
            booking.setReview(null);
            bookingRepository.save(booking);
        }

        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteByBookingId(Long bookingId) {
        if (!reviewRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException("No review found for booking ID: " + bookingId);
        }


        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setReview(null);
        bookingRepository.save(booking);

        reviewRepository.deleteByBookingId(bookingId);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        return mapToResponse(review);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ReviewResponse> findByProfessionalId(Long professionalId) {
        return reviewRepository.findByProfessionalId(professionalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public List<ReviewResponse> findByReviewerId(Long reviewerId) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        if (!isAdmin && !reviewerId.equals(currentUserId)) {
            throw new RuntimeException("You can only view your own reviews");
        }

        return reviewRepository.findByReviewerId(reviewerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ReviewResponse> findByRatingBetween(int min, int max) {
        if (min > max || min < 1 || max > 5) {
            throw new RuntimeException("Invalid rating range (must be between 1 and 5)");
        }

        return reviewRepository.findByRatingBetween(min, max).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private ReviewResponse mapToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setBookingId(review.getBooking() != null ? review.getBooking().getId() : null);
        response.setReviewerId(review.getReviewer() != null ? review.getReviewer().getId() : null);
        response.setProfessionalId(review.getProfessional() != null ? review.getProfessional().getId() : null);
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());


        if (review.getBooking() != null) {
            Booking booking = review.getBooking();
            response.setBooking(BookingResponse.builder()
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
                    .build());
        }


        if (review.getReviewer() != null) {
            User reviewer = review.getReviewer();
            response.setReviewer(UserResponse.builder()
                    .id(reviewer.getId())
                    .firstName(reviewer.getFirstName())
                    .lastName(reviewer.getLastName())
                    .email(reviewer.getEmail())
                    .phoneNumber(reviewer.getPhoneNumber())
                    .role(reviewer.getRole())
                    .status(reviewer.getStatus())
                    .createdAt(reviewer.getCreatedAt())
                    .updatedAt(reviewer.getUpdatedAt())
                    .build());
        }


        if (review.getProfessional() != null) {
            Professional professional = review.getProfessional();
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
