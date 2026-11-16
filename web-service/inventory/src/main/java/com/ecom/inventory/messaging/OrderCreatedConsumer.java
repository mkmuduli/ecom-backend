package com.ecom.inventory.messaging;

import com.ecom.inventory.event.OrderCreatedEvent;
import com.ecom.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    public OrderCreatedConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void consume(OrderCreatedEvent event) {

        System.out.println(
                "Received OrderCreatedEvent: " + event);

        inventoryService.reserveStock(
                event.orderId(),
                event.productId(),
                event.quantity(),
                event.amount());
    }
}
