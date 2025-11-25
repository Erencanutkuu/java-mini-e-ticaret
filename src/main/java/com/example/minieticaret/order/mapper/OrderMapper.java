package com.example.minieticaret.order.mapper;

import com.example.minieticaret.common.mapper.MapStructConfig;
import com.example.minieticaret.order.domain.Order;
import com.example.minieticaret.order.domain.OrderItem;
import com.example.minieticaret.order.dto.OrderItemResponse;
import com.example.minieticaret.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface OrderMapper {

    @Mapping(target = "items", expression = "java(mapItems(order.getItems()))")
    @Mapping(target = "addressId", source = "address.id")
    OrderResponse toResponse(Order order);

    default List<OrderItemResponse> mapItems(List<OrderItem> items) {
        return items.stream()
                .map(i -> new OrderItemResponse(
                        i.getId(),
                        i.getProduct().getId(),
                        i.getProductNameSnapshot(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getCurrency()
                ))
                .toList();
    }
}
