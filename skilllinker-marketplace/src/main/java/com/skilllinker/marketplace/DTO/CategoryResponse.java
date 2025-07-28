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
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;

    // Nested for relationships
    private List<ServiceResponse> services;
}
