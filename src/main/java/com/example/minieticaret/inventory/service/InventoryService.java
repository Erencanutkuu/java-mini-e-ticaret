package com.example.minieticaret.inventory.service;

import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.order.domain.Order;

import java.util.List;

public interface InventoryService {

    void reserveForCartItems(List<CartItem> items);

    void releaseForOrder(Order order);
}
