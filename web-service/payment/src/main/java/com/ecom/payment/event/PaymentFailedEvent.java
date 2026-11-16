package com.ecom.payment.event;

public record PaymentFailedEvent(
        Long orderId,
        String reason) {
}