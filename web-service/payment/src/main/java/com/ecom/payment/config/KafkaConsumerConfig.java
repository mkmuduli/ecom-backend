package com.ecom.payment.config;

import com.ecom.payment.event.InventoryReservedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, InventoryReservedEvent> consumerFactory() {

        JsonDeserializer<InventoryReservedEvent> deserializer = new JsonDeserializer<>(
                InventoryReservedEvent.class);

        deserializer.addTrustedPackages(
                "com.ecom.payment.event");

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092");

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "payment-service");

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, InventoryReservedEvent> consumerFactory) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, InventoryReservedEvent>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
