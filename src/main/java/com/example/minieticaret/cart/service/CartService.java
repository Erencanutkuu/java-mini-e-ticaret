package com.example.minieticaret.cart.service;

import com.example.minieticaret.cart.dto.CartItemRequest;
import com.example.minieticaret.cart.dto.CartResponse;

import java.util.UUID;

public interface CartService {
    CartResponse getCart(UUID userId);

    CartResponse addItem(UUID userId, CartItemRequest request);

    CartResponse updateItem(UUID userId, UUID itemId, CartItemRequest request);

    void removeItem(UUID userId, UUID itemId);

    void clear(UUID userId);
}
