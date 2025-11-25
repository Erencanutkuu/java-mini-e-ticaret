package com.example.minieticaret.inventory.port;

import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.order.domain.Order;

import java.util.List;

public interface InventoryPort {
    void reserveForCartItems(List<CartItem> items);
    void releaseForOrder(Order order);
}
