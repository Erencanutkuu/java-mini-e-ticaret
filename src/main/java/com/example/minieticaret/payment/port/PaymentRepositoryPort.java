package com.example.minieticaret.payment.port;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.payment.domain.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findByOrder(Order order);
    Optional<Payment> findById(UUID id);
}
