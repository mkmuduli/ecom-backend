package com.ecom.order.messaging;

import com.ecom.events.OrderCreatedEvent;
import com.ecom.events.OrderFailedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final String TOPIC_CREATED = "order.created";
    private static final String TOPIC_FAILED = "order.failed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC_CREATED,
                String.valueOf(event.orderId()),
                event);
    }

    public void publishOrderFailed(OrderFailedEvent event) {
        kafkaTemplate.send(
                TOPIC_FAILED,
                String.valueOf(event.orderId()),
                event);
    }
}
