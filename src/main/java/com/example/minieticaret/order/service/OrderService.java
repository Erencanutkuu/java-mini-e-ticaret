package com.example.minieticaret.order.service;

import com.example.minieticaret.order.domain.OrderStatus;
import com.example.minieticaret.order.dto.CheckoutRequest;
import com.example.minieticaret.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(UUID userId, CheckoutRequest request);

    List<OrderResponse> listForUser(UUID userId);

    List<OrderResponse> listByStatus(OrderStatus status);

    OrderResponse updateStatus(UUID orderId, OrderStatus status);
}
