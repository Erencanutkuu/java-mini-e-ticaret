package com.example.minieticaret.payment.service;

import com.example.minieticaret.order.dto.OrderResponse;

import java.util.UUID;

public interface PaymentService {

    OrderResponse capturePayment(UUID orderId);

    OrderResponse refundPayment(UUID orderId);
}
