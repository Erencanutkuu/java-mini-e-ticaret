package com.example.minieticaret.order.repository;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrder(Order order);
}
