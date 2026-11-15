package com.ecom.order.dto;

import com.ecom.order.entity.Order;
import com.ecom.order.entity.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal amount,
        OrderStatus status) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus());
    }
}
