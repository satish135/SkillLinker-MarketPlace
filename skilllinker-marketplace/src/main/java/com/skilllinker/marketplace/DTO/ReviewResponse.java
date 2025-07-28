package com.skilllinker.marketplace.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long bookingId;
    private Long reviewerId;
    private Long professionalId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // Nested for relationships
    private BookingResponse booking;
    private UserResponse reviewer;
    private ProfessionalProfileResponse professional;
}
