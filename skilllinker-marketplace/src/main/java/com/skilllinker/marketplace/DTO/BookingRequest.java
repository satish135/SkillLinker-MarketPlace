package com.skilllinker.marketplace.DTO;

import com.skilllinker.marketplace.Enums.BookingStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotNull(message = "Booking date is required")
    private LocalDateTime bookingDate;

    @NotNull(message = "Status is required")
    private BookingStatus status;

    @NotNull(message = "Total fee is required")
    @DecimalMin(value = "0.0", message = "Total fee cannot be negative")
    private BigDecimal totalFee;

    @NotNull(message = "Payment status is required")
    private String paymentStatus;

    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Professional ID is required")
    private Long professionalId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;
}

