package com.example.minieticaret.cart.mapper;

import com.example.minieticaret.cart.domain.Cart;
import com.example.minieticaret.cart.domain.CartItem;
import com.example.minieticaret.cart.dto.CartItemResponse;
import com.example.minieticaret.cart.dto.CartResponse;
import com.example.minieticaret.common.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CartMapper {

    @Mapping(target = "items", expression = "java(mapItems(cart.getItems()))")
    @Mapping(target = "total", expression = "java(calculateTotal(cart.getItems()))")
    @Mapping(target = "currency", expression = "java(resolveCurrency(cart.getItems()))")
    CartResponse toResponse(Cart cart);

    default List<CartItemResponse> mapItems(List<CartItem> items) {
        return items.stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceSnapshot(),
                        item.getCurrency()
                ))
                .toList();
    }

    default BigDecimal calculateTotal(List<CartItem> items) {
        return items.stream()
                .map(i -> i.getPriceSnapshot().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default String resolveCurrency(List<CartItem> items) {
        return items.stream().findFirst().map(CartItem::getCurrency).orElse("TRY");
    }
}
