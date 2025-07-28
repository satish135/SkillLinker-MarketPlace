package com.skilllinker.marketplace.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long customerId;
    private Long professionalId;
    private Long serviceId;
    private LocalDateTime bookingDate;
    private String status;
    private BigDecimal totalFee;
    private String paymentStatus;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested responses for relationships
    private UserResponse customer;
    private ProfessionalProfileResponse professional;
    private ServiceResponse service;
    private TransactionResponse transaction;
    private ReviewResponse review;
}
