package com.example.minieticaret.order.service;

import com.example.minieticaret.order.domain.Order;

public interface OrderPaymentInitializer {
    void createPendingPayment(Order order);
}
