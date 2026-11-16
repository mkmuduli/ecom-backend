package com.ecom.events;

import java.math.BigDecimal;

public record OrderCreatedEvent(
                String eventId,
                Long orderId,
                Long productId,
                Integer quantity,
                BigDecimal amount) {
}
