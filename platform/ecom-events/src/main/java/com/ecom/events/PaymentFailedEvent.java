package com.ecom.events;

public record PaymentFailedEvent(
        Long orderId,
        String reason) {
}
