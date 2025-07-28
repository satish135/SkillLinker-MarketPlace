package com.skilllinker.marketplace.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "professionals")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Categories are required")
    private String categories;

    @NotBlank(message = "Skills are required")
    private String skills;

    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer experienceYear;

    private String document;

    @Min(value = 0, message = "Profile views cannot be negative")
    private Integer profileViews;

    private Boolean activeStatus;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();


    @OneToMany(mappedBy = "professional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}
