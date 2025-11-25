package com.example.minieticaret.order.dto;

import com.example.minieticaret.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull OrderStatus status
) {
}
