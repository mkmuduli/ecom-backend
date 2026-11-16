package com.ecom.order.event;

public record OrderFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason) {
}
