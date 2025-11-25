package com.example.minieticaret.order.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutRequest(
        @NotNull UUID addressId
) {
}
