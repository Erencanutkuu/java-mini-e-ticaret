package com.example.minieticaret.catalog.dto;

import com.example.minieticaret.catalog.domain.ProductStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        String sku,
        UUID categoryId,
        String categoryName,
        Integer stockQuantity,
        ProductStatus status,
        List<String> images
) {
}
