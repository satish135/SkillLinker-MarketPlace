package com.skilllinker.marketplace.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfessionalProfileRequest {

    @NotBlank(message = "Categories are required")
    private String categories;

    @NotBlank(message = "Skills are required")
    private String skills;

    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYear;

    private String document;  // Optional

    @Min(value = 0, message = "Profile views cannot be negative")
    private Integer profileViews;

    private Boolean activeStatus;
}

