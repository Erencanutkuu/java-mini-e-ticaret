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
import com.example.minieticaret.payment.port.PaymentProviderPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepositoryPort orderRepository;
    private final PaymentProviderPort paymentProvider;
    private final InventoryPort inventoryService;
    private final OrderMapper orderMapper;

    public PaymentServiceImpl(OrderRepositoryPort orderRepository,
                              PaymentProviderPort paymentProvider,
                              InventoryPort inventoryService,
                              OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.paymentProvider = paymentProvider;
        this.inventoryService = inventoryService;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse capturePayment(UUID orderId) {
        Order order = findOrder(orderId);
        OrderStatusTransitions.validate(order.getStatus(), OrderStatus.PAID);
        paymentProvider.capture(order);
        order.setStatus(OrderStatus.PAID);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse refundPayment(UUID orderId) {
        Order order = findOrder(orderId);
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException(ApiErrorCode.ORDER_INVALID_TRANSITION, HttpStatus.BAD_REQUEST, "Bu durumda iade yapilamaz");
        }
        OrderStatusTransitions.validate(order.getStatus(), OrderStatus.REFUNDED);
        paymentProvider.refund(order);
        order.setStatus(OrderStatus.REFUNDED);

        // Stok iadesi
        inventoryService.releaseForOrder(order);

        return orderMapper.toResponse(order);
    }

    private Order findOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND, "Siparis bulunamadi"));
    }
}
