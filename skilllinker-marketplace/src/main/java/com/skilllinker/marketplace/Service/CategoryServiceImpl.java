package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.CategoryRequest;
import com.skilllinker.marketplace.DTO.CategoryResponse;
import com.skilllinker.marketplace.DTO.ServiceResponse;
import com.skilllinker.marketplace.Entity.Category;
import com.skilllinker.marketplace.Entity.Service;
import com.skilllinker.marketplace.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category = categoryRepository.save(category);

        return mapToResponse(category);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category = categoryRepository.save(category);

        return mapToResponse(category);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        return mapToResponse(category);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public CategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + name));

        return mapToResponse(category);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // FIXED: Full mapping with nested services (dynamic, handles nulls)
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());

        // Nested services
        if (category.getServices() != null) {
            response.setServices(category.getServices().stream()
                    .map(this::mapToServiceResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    // Helper for nested ServiceResponse
    private ServiceResponse mapToServiceResponse(Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .minBookingFee(service.getMinBookingFee())
                .build();
    }
}
