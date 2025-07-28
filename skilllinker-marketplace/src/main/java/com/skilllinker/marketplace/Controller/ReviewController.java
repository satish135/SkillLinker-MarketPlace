package com.skilllinker.marketplace.Controller;

import com.skilllinker.marketplace.DTO.ReviewRequest;
import com.skilllinker.marketplace.DTO.ReviewResponse;
import com.skilllinker.marketplace.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id, @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByBookingId(@PathVariable Long bookingId) {
        reviewService.deleteByBookingId(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/professional/{professionalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewResponse>> findByProfessionalId(@PathVariable Long professionalId) {
        List<ReviewResponse> responses = reviewService.findByProfessionalId(professionalId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<ReviewResponse>> findByReviewerId(@PathVariable Long reviewerId) {
        List<ReviewResponse> responses = reviewService.findByReviewerId(reviewerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/rating")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewResponse>> findByRatingBetween(@RequestParam int min, @RequestParam int max) {
        List<ReviewResponse> responses = reviewService.findByRatingBetween(min, max);
        return ResponseEntity.ok(responses);
    }
}
