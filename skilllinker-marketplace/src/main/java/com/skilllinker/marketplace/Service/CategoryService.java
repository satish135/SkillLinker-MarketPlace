package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.CategoryRequest;
import com.skilllinker.marketplace.DTO.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse findByName(String name);

    List<CategoryResponse> findAll();
}
