package com.ecom.payment.event;

import java.math.BigDecimal;

public record PaymentSuccessEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount) {
}
