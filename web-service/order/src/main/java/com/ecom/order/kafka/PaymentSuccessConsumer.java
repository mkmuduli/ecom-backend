package com.ecom.order.kafka;

import com.ecom.order.event.PaymentSuccessEvent;
import com.ecom.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentSuccessConsumer {

    private final OrderService orderService;

    public PaymentSuccessConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "payment.success", groupId = "order-service", containerFactory = "paymentSuccessKafkaListenerContainerFactory")
    public void consume(PaymentSuccessEvent event) {

        System.out.println(
                "Received PaymentSuccessEvent: " + event);

        orderService.markOrderCompleted(event.orderId());
    }
}
