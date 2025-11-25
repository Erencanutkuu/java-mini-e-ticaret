package com.example.minieticaret.catalog.service;

import com.example.minieticaret.catalog.dto.CategoryRequest;
import com.example.minieticaret.catalog.dto.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(UUID id, CategoryRequest request);

    void delete(UUID id);

    List<CategoryResponse> listAll();

    CategoryResponse getBySlug(String slug);
}
