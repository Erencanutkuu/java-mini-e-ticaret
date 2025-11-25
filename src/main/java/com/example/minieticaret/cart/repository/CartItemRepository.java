package com.example.minieticaret.cart.repository;

import com.example.minieticaret.cart.domain.Cart;
import com.example.minieticaret.cart.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByCart(Cart cart);
    void deleteByCart(Cart cart);
}
