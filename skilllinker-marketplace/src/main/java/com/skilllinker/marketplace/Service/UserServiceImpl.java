package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.ProfessionalProfileResponse;
import com.skilllinker.marketplace.DTO.ReviewResponse;
import com.skilllinker.marketplace.DTO.UserResponse;
import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Entity.Professional;
import com.skilllinker.marketplace.Entity.Review;
import com.skilllinker.marketplace.Entity.User;
import com.skilllinker.marketplace.Enums.Role;
import com.skilllinker.marketplace.Enums.Status;
import com.skilllinker.marketplace.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return mapToResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUserByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUserByStatus(Status status) {
        return userRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }


    private UserResponse mapToResponse(User user) {
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();


        if (user.getProfessional() != null) {
            Professional professional = user.getProfessional();
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


        if (user.getBookings() != null) {
            response.setBookings(user.getBookings().stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList()));
        }


        if (user.getReviews() != null) {
            response.setReviews(user.getReviews().stream()
                    .map(this::mapToReviewResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }


    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
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
    }


    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking() != null ? review.getBooking().getId() : null)
                .reviewerId(review.getReviewer() != null ? review.getReviewer().getId() : null)
                .professionalId(review.getProfessional() != null ? review.getProfessional().getId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
