package com.example.minieticaret.catalog.service;

import com.example.minieticaret.catalog.dto.ProductRequest;
import com.example.minieticaret.catalog.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    ProductResponse update(UUID id, ProductRequest request);

    void delete(UUID id);

    ProductResponse getById(UUID id);

    ProductResponse getBySku(String sku);

    List<ProductResponse> list(UUID categoryId);
}
