package com.example.minieticaret.payment.adapter;

import com.example.minieticaret.common.exception.ApiErrorCode;
import com.example.minieticaret.common.exception.BusinessException;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.payment.domain.Payment;
import com.example.minieticaret.payment.domain.PaymentProvider;
import com.example.minieticaret.payment.domain.PaymentStatus;
import com.example.minieticaret.payment.port.PaymentProviderPort;
import com.example.minieticaret.payment.port.PaymentRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MockPaymentProvider implements PaymentProviderPort {

    private final PaymentRepositoryPort paymentRepository;

    public MockPaymentProvider(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment createPending(Order order) {
        return paymentRepository.findByOrder(order)
                .orElseGet(() -> paymentRepository.save(
                        Payment.builder()
                                .order(order)
                                .provider(PaymentProvider.MOCK)
                                .status(PaymentStatus.PENDING)
                                .build()
                ));
    }

    @Override
    public Payment capture(Order order) {
        Payment payment = findPayment(order);
        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new BusinessException(ApiErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, "Odeme zaten islenmis");
        }
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setPaidAt(Instant.now());
        return paymentRepository.save(payment);
    }

    @Override
    public Payment refund(Order order) {
        Payment payment = findPayment(order);
        if (payment.getStatus() != PaymentStatus.CAPTURED) {
            throw new BusinessException(ApiErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, "Odeme capture edilmemis");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    private Payment findPayment(Order order) {
        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.PAYMENT_NOT_FOUND, HttpStatus.NOT_FOUND, "Odeme kaydi yok"));
    }
}
