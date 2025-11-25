package com.example.minieticaret.payment.service;

import com.example.minieticaret.common.exception.ApiErrorCode;
import com.example.minieticaret.common.exception.BusinessException;
import com.example.minieticaret.inventory.port.InventoryPort;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderStatus;
import com.example.minieticaret.order.domain.OrderStatusTransitions;
import com.example.minieticaret.order.dto.OrderResponse;
import com.example.minieticaret.order.mapper.OrderMapper;
import com.example.minieticaret.order.port.OrderRepositoryPort;
import com.example.minieticaret.payment.domain.Payment;
import com.example.minieticaret.payment.domain.PaymentStatus;
import com.example.minieticaret.payment.port.PaymentRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepositoryPort orderRepository;
    private final PaymentRepositoryPort paymentRepository;
    private final InventoryPort inventoryService;
    private final OrderMapper orderMapper;

    public PaymentServiceImpl(OrderRepositoryPort orderRepository,
                              PaymentRepositoryPort paymentRepository,
                              InventoryPort inventoryService,
                              OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.inventoryService = inventoryService;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse capturePayment(UUID orderId) {
        Order order = findOrder(orderId);
        Payment payment = findPayment(order);

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new BusinessException(ApiErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, "Odeme zaten islenmis");
        }
        OrderStatusTransitions.validate(order.getStatus(), OrderStatus.PAID);

        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setPaidAt(Instant.now());
        order.setStatus(OrderStatus.PAID);

        paymentRepository.save(payment);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse refundPayment(UUID orderId) {
        Order order = findOrder(orderId);
        Payment payment = findPayment(order);

        if (payment.getStatus() != PaymentStatus.CAPTURED) {
            throw new BusinessException(ApiErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, "Odeme capture edilmemis");
        }
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException(ApiErrorCode.ORDER_INVALID_TRANSITION, HttpStatus.BAD_REQUEST, "Bu durumda iade yapilamaz");
        }

        OrderStatusTransitions.validate(order.getStatus(), OrderStatus.REFUNDED);
        payment.setStatus(PaymentStatus.REFUNDED);
        order.setStatus(OrderStatus.REFUNDED);

        // Stok iadesi
        inventoryService.releaseForOrder(order);
        paymentRepository.save(payment);

        return orderMapper.toResponse(order);
    }

    private Order findOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND, "Siparis bulunamadi"));
    }

    private Payment findPayment(Order order) {
        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.PAYMENT_NOT_FOUND, HttpStatus.NOT_FOUND, "Odeme kaydi yok"));
    }
}
