package com.example.minieticaret.order;

import com.example.minieticaret.auth.domain.RoleName;
import com.example.minieticaret.cart.dto.CartItemRequest;
import com.example.minieticaret.cart.service.CartService;
import com.example.minieticaret.catalog.domain.Category;
import com.example.minieticaret.catalog.domain.Product;
import com.example.minieticaret.customer.dto.AddressRequest;
import com.example.minieticaret.customer.service.AddressService;
import com.example.minieticaret.order.domain.OrderStatus;
import com.example.minieticaret.order.dto.CheckoutRequest;
import com.example.minieticaret.order.dto.OrderResponse;
import com.example.minieticaret.order.repository.OrderRepository;
import com.example.minieticaret.order.service.OrderService;
import com.example.minieticaret.payment.domain.Payment;
import com.example.minieticaret.payment.domain.PaymentStatus;
import com.example.minieticaret.payment.repository.PaymentRepository;
import com.example.minieticaret.payment.service.PaymentService;
import com.example.minieticaret.testsupport.TestDataFactory;
import com.example.minieticaret.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderFlowIntegrationTest {

    @Autowired
    private TestDataFactory testDataFactory;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    private UUID userId;
    private UUID productId;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        testDataFactory.ensureRole(RoleName.USER);
        var user = testDataFactory.ensureUser("test@example.com", "Test", "User", "pw", Set.of(RoleName.USER));
        userId = user.getId();

        Category category = testDataFactory.ensureCategory("Elektronik", "elektronik");
        Product product = testDataFactory.ensureProduct("SKU-1", category, new BigDecimal("100.00"), "TRY", 10);
        productId = product.getId();

        addressId = addressService.create(
                new AddressRequest("Line1", null, "Istanbul", "TR", "34000", "555"),
                userId
        ).id();
    }

    @Test
    void checkout_capture_refund_flow_should_update_stock_and_statuses() {
        cartService.addItem(userId, new CartItemRequest(productId, 2));

        OrderResponse checkout = orderService.checkout(userId, new CheckoutRequest(addressId));
        assertThat(checkout.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(checkout.total()).isEqualByComparingTo("200.00");

        Product afterCheckout = productRepository.findById(productId).orElseThrow();
        assertThat(afterCheckout.getStockQuantity()).isEqualTo(8);

        OrderResponse captured = paymentService.capturePayment(checkout.id());
        assertThat(captured.status()).isEqualTo(OrderStatus.PAID);
        Payment payment = paymentRepository.findByOrder(orderRepository.findById(checkout.id()).orElseThrow())
                .orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CAPTURED);

        OrderResponse refunded = paymentService.refundPayment(checkout.id());
        assertThat(refunded.status()).isEqualTo(OrderStatus.REFUNDED);
        Product afterRefund = productRepository.findById(productId).orElseThrow();
        assertThat(afterRefund.getStockQuantity()).isEqualTo(10);
    }
}
