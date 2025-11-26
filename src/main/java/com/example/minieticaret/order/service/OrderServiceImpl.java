package com.example.minieticaret.order.service;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.auth.repository.UserRepository;
import com.example.minieticaret.cart.domain.Cart;
import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.cart.repository.CartItemRepository;
import com.example.minieticaret.cart.repository.CartRepository;
import com.example.minieticaret.customer.domain.Address;
import com.example.minieticaret.customer.repository.AddressRepository;
import com.example.minieticaret.common.exception.ApiErrorCode;
import com.example.minieticaret.common.exception.BusinessException;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderItem;
import com.example.minieticaret.order.domain.OrderStatus;
import com.example.minieticaret.order.domain.OrderStatusTransitions;
import com.example.minieticaret.order.dto.CheckoutRequest;
import com.example.minieticaret.order.dto.OrderResponse;
import com.example.minieticaret.order.mapper.OrderMapper;
import com.example.minieticaret.order.port.OrderRepositoryPort;
import com.example.minieticaret.inventory.port.InventoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderRepositoryPort orderRepository;
    private final InventoryPort inventoryService;
    private final OrderMapper orderMapper;
    private final OrderPricingService orderPricingService;
    private final OrderPaymentInitializer orderPaymentInitializer;
    private final OrderPaymentStatusService orderPaymentStatusService;

    public OrderServiceImpl(CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            AddressRepository addressRepository,
                            UserRepository userRepository,
                            OrderRepositoryPort orderRepository,
                            InventoryPort inventoryService,
                            OrderMapper orderMapper,
                            OrderPricingService orderPricingService,
                            OrderPaymentInitializer orderPaymentInitializer,
                            OrderPaymentStatusService orderPaymentStatusService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.orderMapper = orderMapper;
        this.orderPricingService = orderPricingService;
        this.orderPaymentInitializer = orderPaymentInitializer;
        this.orderPaymentStatusService = orderPaymentStatusService;
    }

    @Override
    @Transactional
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        User user = findUser(userId);
        Address address = findAddress(request.addressId(), userId);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, "Sepet bos"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ApiErrorCode.INVALID_ARGUMENT, HttpStatus.BAD_REQUEST, "Sepet bos");
        }

        // Stok kontrol + rezervasyon
        inventoryService.reserveForCartItems(cart.getItems());

        Order order = Order.builder()
                .user(user)
                .address(address)
                .status(OrderStatus.PENDING)
                .currency(resolveCurrency(cart.getItems()))
                .total(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        orderPricingService.applyItemsAndCalculateTotal(order, cart.getItems());

        Order saved = orderRepository.save(order);
        orderPaymentInitializer.createPendingPayment(saved);

        // Sepeti temizle
        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listForUser(UUID userId) {
        User user = findUser(userId);
        return orderRepository.findByUser(user)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.ORDER_NOT_FOUND, HttpStatus.NOT_FOUND, "Siparis bulunamadi"));
        OrderStatus previous = order.getStatus();
        OrderStatusTransitions.validate(previous, status);
        order.setStatus(status);

        // Stok iadesi: sadece cancel/refund durumunda, daha önce iade edilmemişse uygula
        if ((status == OrderStatus.CANCELLED || status == OrderStatus.REFUNDED)
                && previous != OrderStatus.CANCELLED && previous != OrderStatus.REFUNDED) {
            inventoryService.releaseForOrder(order);
        }

        // Ödeme durumunu güncelle (mock)
        orderPaymentStatusService.syncPaymentWithStatus(order, status);

        return orderMapper.toResponse(order);
    }

    private String resolveCurrency(List<CartItem> items) {
        return items.stream().findFirst()
                .map(CartItem::getCurrency)
                .orElse("TRY");
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND, "Kullanici bulunamadi"));
    }

    private Address findAddress(UUID addressId, UUID userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ApiErrorCode.ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND, "Adres bulunamadi"));
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException(ApiErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, "Adres size ait degil");
        }
        return address;
    }
}
