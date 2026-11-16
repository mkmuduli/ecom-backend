package com.ecom.inventory.config;

import com.ecom.inventory.event.OrderCreatedEvent;
import com.ecom.inventory.event.OrderFailedEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

        @Bean
        public ConsumerFactory<String, OrderCreatedEvent> consumerFactory() {

                JacksonJsonDeserializer<OrderCreatedEvent> deserializer = new JacksonJsonDeserializer<>(
                                OrderCreatedEvent.class);

                deserializer.addTrustedPackages("com.ecom.inventory.event");
                deserializer.setUseTypeHeaders(false);

                Map<String, Object> config = new HashMap<>();

                config.put(
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "localhost:9092");

                config.put(
                                ConsumerConfig.GROUP_ID_CONFIG,
                                "inventory-service");

                config.put(
                                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                                StringDeserializer.class);

                config.put(
                                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                                JacksonJsonDeserializer.class);

                return new DefaultKafkaConsumerFactory<>(
                                config,
                                new StringDeserializer(),
                                deserializer);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> kafkaListenerContainerFactory(
                        ConsumerFactory<String, OrderCreatedEvent> consumerFactory) {

                ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

                factory.setConsumerFactory(consumerFactory);

                return factory;
        }

        @Bean
        public ConsumerFactory<String, OrderFailedEvent> orderFailedConsumerFactory() {

                JacksonJsonDeserializer<OrderFailedEvent> deserializer = new JacksonJsonDeserializer<>(OrderFailedEvent.class);

                deserializer.setUseTypeHeaders(false);
                deserializer.addTrustedPackages("*");

                Map<String, Object> props = new HashMap<>();

                props.put(
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "localhost:9092");

                props.put(
                                ConsumerConfig.GROUP_ID_CONFIG,
                                "inventory-service");

                props.put(
                                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                                StringDeserializer.class);

                return new DefaultKafkaConsumerFactory<>(
                                props,
                                new StringDeserializer(),
                                deserializer);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, OrderFailedEvent> orderFailedKafkaListenerContainerFactory(
                        ConsumerFactory<String, OrderFailedEvent> consumerFactory) {

                ConcurrentKafkaListenerContainerFactory<String, OrderFailedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

                factory.setConsumerFactory(consumerFactory);

                return factory;
        }
}
