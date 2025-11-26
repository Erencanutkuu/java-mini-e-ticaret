package com.example.minieticaret.order.service;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.payment.domain.Payment;
import com.example.minieticaret.payment.domain.PaymentProvider;
import com.example.minieticaret.payment.domain.PaymentStatus;
import com.example.minieticaret.payment.port.PaymentRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentInitializerImpl implements OrderPaymentInitializer {

    private final PaymentRepositoryPort paymentRepository;

    public OrderPaymentInitializerImpl(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void createPendingPayment(Order order) {
        Payment payment = Payment.builder()
                .order(order)
                .provider(PaymentProvider.MOCK)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
    }
}
