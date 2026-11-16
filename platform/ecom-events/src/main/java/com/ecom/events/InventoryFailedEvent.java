package com.ecom.events;

public record InventoryFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason) {
}
