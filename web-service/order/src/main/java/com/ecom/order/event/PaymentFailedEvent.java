package com.ecom.order.event;

public record PaymentFailedEvent(
        Long orderId,
        String reason) {
}