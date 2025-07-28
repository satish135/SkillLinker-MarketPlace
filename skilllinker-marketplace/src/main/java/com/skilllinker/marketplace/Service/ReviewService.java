package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.ReviewRequest;
import com.skilllinker.marketplace.DTO.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse updateReview(Long id, ReviewRequest request);

    void deleteReview(Long id);

    void deleteByBookingId(Long bookingId);

    ReviewResponse getReviewById(Long id);

    List<ReviewResponse> findByProfessionalId(Long professionalId);

    List<ReviewResponse> findByReviewerId(Long reviewerId);

    List<ReviewResponse> findByRatingBetween(int min, int max);
}
