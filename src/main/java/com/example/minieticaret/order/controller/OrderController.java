package com.example.minieticaret.order.controller;

import com.example.minieticaret.order.domain.OrderStatus;
import com.example.minieticaret.order.dto.CheckoutRequest;
import com.example.minieticaret.order.dto.OrderResponse;
import com.example.minieticaret.order.dto.OrderStatusUpdateRequest;
import com.example.minieticaret.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Sepeti siparişe dönüştür")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal(expression = "user.id") UUID userId,
                                                  @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(userId, request));
    }

    @GetMapping
    @Operation(summary = "Kullanıcı siparişlerini listele")
    public ResponseEntity<List<OrderResponse>> listForUser(@AuthenticationPrincipal(expression = "user.id") UUID userId) {
        return ResponseEntity.ok(orderService.listForUser(userId));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Siparişleri statüye göre listele (admin)")
    public ResponseEntity<List<OrderResponse>> listByStatus(@RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.listByStatus(status));
    }

    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Sipariş statüsünü güncelle (admin)")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable UUID orderId,
                                                      @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.status()));
    }
}
