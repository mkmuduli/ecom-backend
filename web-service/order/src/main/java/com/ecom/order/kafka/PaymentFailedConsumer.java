package com.ecom.order.kafka;

import com.ecom.order.event.PaymentFailedEvent;
import com.ecom.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedConsumer {

    private final OrderService orderService;

    public PaymentFailedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service", containerFactory = "paymentFailedKafkaListenerContainerFactory")
    public void consume(PaymentFailedEvent event) {

        System.out.println(
                "Received PaymentFailedEvent: " + event);

        orderService.markOrderFailed(event.orderId());
    }
}
