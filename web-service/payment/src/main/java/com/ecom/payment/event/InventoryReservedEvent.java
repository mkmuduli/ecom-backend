package com.ecom.payment.event;

import java.math.BigDecimal;

public record InventoryReservedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        BigDecimal amount) {
}
