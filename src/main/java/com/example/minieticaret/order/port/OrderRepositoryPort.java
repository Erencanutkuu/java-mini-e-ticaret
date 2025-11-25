package com.example.minieticaret.order.port;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
}
