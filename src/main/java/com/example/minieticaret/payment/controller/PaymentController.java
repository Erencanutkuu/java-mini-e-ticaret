package com.example.minieticaret.payment.controller;

import com.example.minieticaret.order.dto.OrderResponse;
import com.example.minieticaret.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments/mock")
@Tag(name = "Payment (Mock)")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/capture")
    @Operation(summary = "Mock odeme capture (admin)")
    public ResponseEntity<OrderResponse> capture(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.capturePayment(orderId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/refund")
    @Operation(summary = "Mock odeme refund (admin)")
    public ResponseEntity<OrderResponse> refund(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.refundPayment(orderId));
    }
}
