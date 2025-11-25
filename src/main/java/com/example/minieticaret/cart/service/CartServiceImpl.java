package com.example.minieticaret.cart.service;

import com.example.minieticaret.auth.domain.User;
import com.example.minieticaret.auth.repository.UserRepository;
import com.example.minieticaret.cart.domain.Cart;
import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.cart.dto.CartItemRequest;
import com.example.minieticaret.cart.dto.CartResponse;
import com.example.minieticaret.cart.mapper.CartMapper;
import com.example.minieticaret.cart.repository.CartItemRepository;
import com.example.minieticaret.cart.repository.CartRepository;
import com.example.minieticaret.catalog.domain.Product;
import com.example.minieticaret.catalog.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = findProduct(request.productId());

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .priceSnapshot(product.getPrice())
                    .currency(product.getCurrency())
                    .build();
            cart.getItems().add(item);
        }

        Cart saved = cartRepository.save(cart);
        return cartMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID userId, UUID itemId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sepet ogesi bulunamadi"));

        Product product = findProduct(request.productId());
        item.setProduct(product);
        item.setQuantity(request.quantity());
        item.setPriceSnapshot(product.getPrice());
        item.setCurrency(product.getCurrency());

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public void removeItem(UUID userId, UUID itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sepet ogesi bulunamadi"));
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clear(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart(cart);
        cart.getItems().clear();
    }

    private Cart getOrCreateCart(UUID userId) {
        User user = findUser(userId);
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = Cart.builder().user(user).build();
                    return cartRepository.save(c);
                });
    }

    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Urun bulunamadi"));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanici bulunamadi"));
    }
}
