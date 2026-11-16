package com.ecom.payment.messaging;

import com.ecom.events.InventoryReservedEvent;
import com.ecom.payment.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservedConsumer {

    private final PaymentService paymentService;

    public InventoryReservedConsumer(
            PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "inventory.reserved", groupId = "payment-service")
    public void consume(InventoryReservedEvent event) {

        System.out.println(
                "Received InventoryReservedEvent: " + event);

        paymentService.processPayment(
                event.orderId(),
                event.amount());
    }
}
