package com.ecom.inventory.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        BigDecimal amount) {
}