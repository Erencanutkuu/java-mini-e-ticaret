package com.example.minieticaret.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID id,
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal priceSnapshot,
        String currency
) {
}
