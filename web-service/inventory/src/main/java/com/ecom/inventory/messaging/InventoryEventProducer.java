package com.ecom.inventory.messaging;

import com.ecom.inventory.event.InventoryFailedEvent;
import com.ecom.inventory.event.InventoryReservedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReserved(InventoryReservedEvent event) {

        kafkaTemplate.send(
                "inventory.reserved",
                String.valueOf(event.orderId()),
                event);
    }

    public void publishFailed(InventoryFailedEvent event) {

        kafkaTemplate.send(
                "inventory.failed",
                String.valueOf(event.orderId()),
                event);
    }
}