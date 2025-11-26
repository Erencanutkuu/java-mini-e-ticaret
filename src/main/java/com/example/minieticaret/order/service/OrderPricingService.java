package com.example.minieticaret.order.service;

import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.order.domain.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderPricingService {

    /**
     * Sepet ürünlerinden OrderItem'ları oluşturur ve toplam tutarı hesaplar.
     *
     * @param order     hedef sipariş
     * @param cartItems sepet ürünleri
     * @return hesaplanan toplam tutar
     */
    BigDecimal applyItemsAndCalculateTotal(Order order, List<CartItem> cartItems);
}
