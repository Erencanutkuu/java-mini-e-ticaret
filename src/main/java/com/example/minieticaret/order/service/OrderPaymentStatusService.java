package com.example.minieticaret.order.service;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderStatus;

public interface OrderPaymentStatusService {

    /**
     * Sipariş durumuna göre ödeme kaydını senkronize eder (mock senaryosu).
     */
    void syncPaymentWithStatus(Order order, OrderStatus newStatus);
}
