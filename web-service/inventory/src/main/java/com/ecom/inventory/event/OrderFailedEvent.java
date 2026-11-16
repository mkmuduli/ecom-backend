package com.ecom.inventory.event;

public record OrderFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason) {
}
