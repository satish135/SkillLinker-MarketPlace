package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.Config.CustomUserDetails;
import com.skilllinker.marketplace.DTO.BookingResponse;
import com.skilllinker.marketplace.DTO.ProfessionalProfileRequest;
import com.skilllinker.marketplace.DTO.ProfessionalProfileResponse;
import com.skilllinker.marketplace.DTO.ReviewResponse;
import com.skilllinker.marketplace.DTO.UserResponse;
import com.skilllinker.marketplace.Entity.Booking;
import com.skilllinker.marketplace.Entity.Professional;
import com.skilllinker.marketplace.Entity.Review;
import com.skilllinker.marketplace.Entity.User;
import com.skilllinker.marketplace.Repositories.ProfessionalRepository;
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
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ProfessionalProfileResponse createProfessional(ProfessionalProfileRequest request) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();


        Long targetUserId = currentUserId;

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + targetUserId));

        if (professionalRepository.findByUserId(targetUserId).isPresent()) {
            throw new RuntimeException("Professional profile already exists for this user");
        }

        Professional professional = new Professional();
        professional.setCategories(request.getCategories());
        professional.setSkills(request.getSkills());
        professional.setExperienceYear(request.getExperienceYear());
        professional.setDocument(request.getDocument());
        professional.setProfileViews(request.getProfileViews() != null ? request.getProfileViews() : 0);
        professional.setActiveStatus(request.getActiveStatus() != null ? request.getActiveStatus() : true);
        professional.setUser(user);
        professional = professionalRepository.save(professional);

        return mapToResponse(professional);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public ProfessionalProfileResponse updateProfessional(Long id, ProfessionalProfileRequest request) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found with ID: " + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        if (!isAdmin && !professional.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only update your own professional profile");
        }

        professional.setCategories(request.getCategories());
        professional.setSkills(request.getSkills());
        professional.setExperienceYear(request.getExperienceYear());
        professional.setDocument(request.getDocument());
        professional.setProfileViews(request.getProfileViews());
        professional.setActiveStatus(request.getActiveStatus());
        professional = professionalRepository.save(professional);

        return mapToResponse(professional);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('PROFESSIONAL', 'ADMIN')")
    public void updateActiveStatus(Long id, Boolean active) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found with ID: " + id));

        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();

        if (!isAdmin && !professional.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You can only update your own professional status");
        }

        professionalRepository.updateActiveStatus(id, active);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProfessional(Long id) {
        if (!professionalRepository.existsById(id)) {
            throw new RuntimeException("Professional not found with ID: " + id);
        }
        professionalRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ProfessionalProfileResponse getProfessionalById(Long id) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professional not found with ID: " + id));

        return mapToResponse(professional);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ProfessionalProfileResponse> searchByCategories(String category) {
        return professionalRepository.findByCategoriesContaining(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ProfessionalProfileResponse> searchBySkills(String skill) {
        return professionalRepository.findBySkillsContaining(skill)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ProfessionalProfileResponse> findByActiveStatus(Boolean active) {
        return professionalRepository.findByActiveStatus(active)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ProfessionalProfileResponse findByUserId(Long userId) {
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Professional not found for user ID: " + userId));

        return mapToResponse(professional);
    }


    private ProfessionalProfileResponse mapToResponse(Professional professional) {
        ProfessionalProfileResponse response = new ProfessionalProfileResponse();
        response.setId(professional.getId());
        response.setCategories(professional.getCategories());
        response.setSkills(professional.getSkills());
        response.setExperienceYear(professional.getExperienceYear());
        response.setDocument(professional.getDocument());
        response.setProfileViews(professional.getProfileViews());
        response.setActiveStatus(professional.getActiveStatus());

        // Nested user
        if (professional.getUser() != null) {
            User user = professional.getUser();
            response.setUser(UserResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build());
        }

        // Nested bookings
        if (professional.getBookings() != null) {
            response.setBookings(professional.getBookings().stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList()));
        }

        // Nested reviews
        if (professional.getReviews() != null) {
            response.setReviews(professional.getReviews().stream()
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
