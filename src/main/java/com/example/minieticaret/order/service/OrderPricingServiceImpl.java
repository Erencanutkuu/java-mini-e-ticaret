package com.example.minieticaret.order.service;

import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderPricingServiceImpl implements OrderPricingService {

    @Override
    public BigDecimal applyItemsAndCalculateTotal(Order order, List<CartItem> cartItems) {
        order.setItems(new ArrayList<>());
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .productNameSnapshot(cartItem.getProduct().getName())
                    .unitPrice(cartItem.getPriceSnapshot())
                    .quantity(cartItem.getQuantity())
                    .currency(cartItem.getCurrency())
                    .build();
            order.getItems().add(oi);
            total = total.add(cartItem.getPriceSnapshot().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotal(total);
        return total;
    }
}
