package com.skilllinker.marketplace.DTO;

import com.skilllinker.marketplace.Enums.Role;
import com.skilllinker.marketplace.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private ProfessionalProfileResponse professional;
    private List<BookingResponse> bookings;
    private List<ReviewResponse> reviews;
}
