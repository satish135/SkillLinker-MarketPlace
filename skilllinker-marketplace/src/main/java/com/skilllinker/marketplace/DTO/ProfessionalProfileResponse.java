package com.skilllinker.marketplace.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfessionalProfileResponse {

    private Long id;
    private String categories;
    private String skills;
    private Integer experienceYear;
    private String document;
    private Integer profileViews;
    private Boolean activeStatus;


    private UserResponse user;
    private List<BookingResponse> bookings;
    private List<ReviewResponse> reviews;
}
