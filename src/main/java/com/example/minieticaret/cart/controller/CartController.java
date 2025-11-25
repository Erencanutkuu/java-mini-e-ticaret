package com.example.minieticaret.cart.controller;

import com.example.minieticaret.cart.dto.CartItemRequest;
import com.example.minieticaret.cart.dto.CartResponse;
import com.example.minieticaret.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Sepeti getir")
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal(expression = "user.id") UUID userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    @Operation(summary = "Sepete ürün ekle")
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal(expression = "user.id") UUID userId,
                                                @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(userId, request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Sepet öğesini güncelle")
    public ResponseEntity<CartResponse> updateItem(@AuthenticationPrincipal(expression = "user.id") UUID userId,
                                                   @PathVariable UUID itemId,
                                                   @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(userId, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Sepet öğesini sil")
    public ResponseEntity<Void> deleteItem(@AuthenticationPrincipal(expression = "user.id") UUID userId,
                                           @PathVariable UUID itemId) {
        cartService.removeItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    @Operation(summary = "Sepeti temizle")
    public ResponseEntity<Void> clear(@AuthenticationPrincipal(expression = "user.id") UUID userId) {
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }
}
