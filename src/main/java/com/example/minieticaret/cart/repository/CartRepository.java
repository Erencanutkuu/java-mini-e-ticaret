package com.example.minieticaret.cart.repository;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUser(User user);
    boolean existsByUser(User user);
}
