package com.example.minieticaret.payment.adapter;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.payment.domain.Payment;
import com.example.minieticaret.payment.port.PaymentRepositoryPort;
import com.example.minieticaret.payment.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentRepository paymentRepository;

    public PaymentRepositoryAdapter(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByOrder(Order order) {
        return paymentRepository.findByOrder(order);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return paymentRepository.findById(id);
    }
}
