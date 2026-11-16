package com.ecom.inventory.kafka;

import com.ecom.inventory.event.OrderFailedEvent;
import com.ecom.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderFailedConsumer {

    private final InventoryService inventoryService;

    public OrderFailedConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order.failed", groupId = "inventory-service", containerFactory = "orderFailedKafkaListenerContainerFactory")
    public void consume(OrderFailedEvent event) {

        System.out.println(
                "Received OrderFailedEvent: " + event);

        inventoryService.releaseStock(
                event.productId(),
                event.quantity());
    }
}