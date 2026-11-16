package com.ecom.inventory.event;

public record InventoryFailedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason) {
}
