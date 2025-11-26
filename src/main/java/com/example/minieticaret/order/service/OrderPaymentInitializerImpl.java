package com.example.minieticaret.order.service;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.payment.port.PaymentProviderPort;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentInitializerImpl implements OrderPaymentInitializer {

    private final PaymentProviderPort paymentProvider;

    public OrderPaymentInitializerImpl(PaymentProviderPort paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    @Override
    public void createPendingPayment(Order order) {
        paymentProvider.createPending(order);
    }
}
