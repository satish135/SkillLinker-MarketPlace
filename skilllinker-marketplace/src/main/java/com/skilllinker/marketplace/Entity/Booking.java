package com.skilllinker.marketplace.Entity;

import com.skilllinker.marketplace.Enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Booking date is required")
    private LocalDateTime bookingDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private BookingStatus status;

    @NotNull(message = "Total fee is required")
    @DecimalMin(value = "0.0", message = "Total fee cannot be negative")
    private BigDecimal totalFee;

    @NotNull(message = "Payment status is required")
    private String paymentStatus;

    @NotNull(message = "Address is required")
    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Customer is required")
    private User customer;


    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    @NotNull(message = "Professional is required")
    private Professional professional;


    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @NotNull(message = "Service is required")
    private Service service;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id", referencedColumnName = "id")
    private Transaction transaction;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private Review review;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
