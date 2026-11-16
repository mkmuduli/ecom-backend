package com.ecom.payment.messaging;

import com.ecom.payment.event.PaymentFailedEvent;
import com.ecom.payment.event.PaymentSuccessEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishSuccess(PaymentSuccessEvent event) {

        kafkaTemplate.send(
                "payment.success",
                String.valueOf(event.orderId()),
                event);
    }

    public void publishFailed(PaymentFailedEvent event) {

        kafkaTemplate.send(
                "payment.failed",
                String.valueOf(event.orderId()),
                event);
    }
}
