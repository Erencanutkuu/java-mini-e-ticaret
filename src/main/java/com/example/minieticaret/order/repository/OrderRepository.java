package com.example.minieticaret.order.repository;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
}
