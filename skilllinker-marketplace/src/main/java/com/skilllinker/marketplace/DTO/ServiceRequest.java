package com.skilllinker.marketplace.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Minimum booking fee is required")
    @DecimalMin(value = "0.0", message = "Minimum booking fee cannot be negative")
    private BigDecimal minBookingFee;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
