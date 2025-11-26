package com.example.minieticaret.payment.port;

import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.payment.domain.Payment;

/**
 * Ödeme sağlayıcı adaptörü (mock veya gerçek sağlayıcı).
 */
public interface PaymentProviderPort {

    /**
     * Sipariş için pending ödeme kaydı oluşturur (varsa dokunmaz).
     */
    Payment createPending(Order order);

    /**
     * Ödemeyi capture eder, durumunu günceller ve kaydeder.
     */
    Payment capture(Order order);

    /**
     * Capture edilmiş ödemeyi iade eder, durumunu günceller ve kaydeder.
     */
    Payment refund(Order order);
}
