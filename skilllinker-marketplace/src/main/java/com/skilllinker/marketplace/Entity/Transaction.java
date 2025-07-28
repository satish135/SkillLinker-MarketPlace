package com.skilllinker.marketplace.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", message = "Amount cannot be negative")
    private BigDecimal amount;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private LocalDateTime createdAt;


    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @NotNull(message = "Booking is required")
    private Booking booking;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
