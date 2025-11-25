package com.example.minieticaret.catalog.dto;

import com.example.minieticaret.catalog.domain.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductRequest(
        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "Fiyat pozitif olmalidir")
        BigDecimal price,

        @NotBlank
        @Size(min = 3, max = 3, message = "Para birimi 3 harf olmalidir")
        String currency,

        @NotBlank
        @Size(max = 80)
        String sku,

        @NotNull
        UUID categoryId,

        @NotNull
        Integer stockQuantity,

        @NotNull
        ProductStatus status,

        List<@NotBlank @Size(max = 500) String> images
) {
}
