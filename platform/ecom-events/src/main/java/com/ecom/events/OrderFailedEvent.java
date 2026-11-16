package com.ecom.events;

public record OrderFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason) {
}
