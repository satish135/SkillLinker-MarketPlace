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
public class TransactionResponse {

    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;


    private BookingResponse booking;
}
