package com.ecom.order.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import com.ecom.order.event.PaymentFailedEvent;
import com.ecom.order.event.PaymentSuccessEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

        @Bean
        public ConsumerFactory<String, PaymentFailedEvent> paymentFailedConsumerFactory() {
                return consumerFactory(PaymentFailedEvent.class);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> paymentFailedKafkaListenerContainerFactory(
                        ConsumerFactory<String, PaymentFailedEvent> paymentFailedConsumerFactory) {
                var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent>();
                factory.setConsumerFactory(paymentFailedConsumerFactory);
                return factory;
        }

        @Bean
        public ConsumerFactory<String, PaymentSuccessEvent> paymentSuccessConsumerFactory() {
                return consumerFactory(PaymentSuccessEvent.class);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent> paymentSuccessKafkaListenerContainerFactory(
                        ConsumerFactory<String, PaymentSuccessEvent> paymentSuccessConsumerFactory) {
                var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent>();
                factory.setConsumerFactory(paymentSuccessConsumerFactory);
                return factory;
        }

        private <T> ConsumerFactory<String, T> consumerFactory(Class<T> eventType) {
                var deserializer = new JacksonJsonDeserializer<>(eventType);
                deserializer.addTrustedPackages("com.ecom.order.event");
                deserializer.setUseTypeHeaders(false);

                Map<String, Object> config = new HashMap<>();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

                return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
        }

        @Bean
        public ProducerFactory<String, Object> producerFactory() {

                Map<String, Object> config = new HashMap<>();

                config.put(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                                "localhost:9092");

                config.put(
                                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                                StringSerializer.class);

                config.put(
                                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                                JacksonJsonSerializer.class);

                return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate(
                        ProducerFactory<String, Object> producerFactory) {

                return new KafkaTemplate<>(producerFactory);
        }
}
