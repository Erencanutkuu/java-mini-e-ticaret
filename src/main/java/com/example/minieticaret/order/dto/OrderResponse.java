package com.example.minieticaret.order.dto;

import com.example.minieticaret.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        OrderStatus status,
        BigDecimal total,
        String currency,
        UUID addressId,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
