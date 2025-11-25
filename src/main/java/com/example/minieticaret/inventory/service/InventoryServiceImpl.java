package com.example.minieticaret.inventory.service;

import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.catalog.domain.Product;
import com.example.minieticaret.catalog.repository.ProductRepository;
import com.example.minieticaret.common.exception.ApiErrorCode;
import com.example.minieticaret.common.exception.BusinessException;
import com.example.minieticaret.inventory.port.InventoryPort;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderItem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService, InventoryPort {

    private final ProductRepository productRepository;

    public InventoryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void reserveForCartItems(List<CartItem> items) {
        for (CartItem cartItem : items) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException(ApiErrorCode.STOCK_INSUFFICIENT, HttpStatus.BAD_REQUEST,
                        "Yetersiz stok: " + product.getName());
            }
        }
        for (CartItem cartItem : items) {
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    @Transactional
    public void releaseForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }
}
