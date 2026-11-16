package com.ecom.order.event;

import java.math.BigDecimal;

public record PaymentSuccessEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount) {
}
