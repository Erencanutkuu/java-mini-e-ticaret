package com.example.minieticaret.order.service;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderStatus;
import com.example.minieticaret.payment.domain.Payment;
import com.example.minieticaret.payment.domain.PaymentStatus;
import com.example.minieticaret.payment.port.PaymentRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class OrderPaymentStatusServiceImpl implements OrderPaymentStatusService {

    private final PaymentRepositoryPort paymentRepository;

    public OrderPaymentStatusServiceImpl(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void syncPaymentWithStatus(Order order, OrderStatus newStatus) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrder(order);
        if (paymentOpt.isEmpty()) {
            return;
        }
        Payment payment = paymentOpt.get();
        switch (newStatus) {
            case PAID, SHIPPED -> {
                payment.setStatus(PaymentStatus.CAPTURED);
                payment.setPaidAt(Instant.now());
            }
            case REFUNDED -> payment.setStatus(PaymentStatus.REFUNDED);
            case CANCELLED -> payment.setStatus(PaymentStatus.FAILED);
            default -> {
                // diğer durumlarda ödeme dokunulmaz
            }
        }
        paymentRepository.save(payment);
    }
}
