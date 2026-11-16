package com.ecom.events;

import java.math.BigDecimal;

public record PaymentSuccessEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount) {
}
