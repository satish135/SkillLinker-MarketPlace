package com.skilllinker.marketplace.Repositories;

import com.skilllinker.marketplace.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    @Modifying
    @Transactional
    void deleteByBookingId(Long bookingId);


    List<Review> findByProfessionalId(Long professionalId);


    List<Review> findByReviewerId(Long reviewerId);


    boolean existsByBookingId(Long bookingId);


    List<Review> findByRatingBetween(int min, int max);
}
