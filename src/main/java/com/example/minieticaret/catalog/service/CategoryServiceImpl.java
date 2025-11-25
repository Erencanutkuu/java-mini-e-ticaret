package com.example.minieticaret.catalog.service;

import com.example.minieticaret.catalog.domain.Category;
import com.example.minieticaret.catalog.dto.CategoryRequest;
import com.example.minieticaret.catalog.dto.CategoryResponse;
import com.example.minieticaret.catalog.mapper.CategoryMapper;
import com.example.minieticaret.catalog.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Slug zaten kullaniliyor");
        }
        Category category = categoryMapper.toEntity(request);
        if (request.parentId() != null) {
            category.setParent(findCategory(request.parentId()));
        }
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = findCategory(id);
        if (!category.getSlug().equals(request.slug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Slug zaten kullaniliyor");
        }
        categoryMapper.updateEntity(request, category);
        category.setParent(request.parentId() != null ? findCategory(request.parentId()) : null);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Category category = findCategory(id);
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadi"));
        return categoryMapper.toResponse(category);
    }

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadi"));
    }
}
