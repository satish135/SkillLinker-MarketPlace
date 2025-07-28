package com.skilllinker.marketplace.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @NotNull(message = "Rating is required")
    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;


    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @NotNull(message = "Booking is required")
    private Booking booking;


    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    @NotNull(message = "Reviewer is required")
    private User reviewer;


    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    @NotNull(message = "Professional is required")
    private Professional professional;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

