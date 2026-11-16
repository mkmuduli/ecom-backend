package com.ecom.order.service;

import com.ecom.order.dto.CreateOrderRequest;
import com.ecom.order.dto.OrderResponse;
import com.ecom.order.entity.Order;
import com.ecom.order.entity.OrderStatus;
import com.ecom.events.OrderCreatedEvent;
import com.ecom.order.messaging.OrderEventProducer;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.grpc.ProductPriceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final ProductPriceClient productPriceClient;

    public OrderService(
            OrderRepository orderRepository,
            OrderEventProducer orderEventProducer,
            ProductPriceClient productPriceClient) {

        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
        this.productPriceClient = productPriceClient;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        BigDecimal unitPrice = productPriceClient.getPrice(request.productId());
        BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(request.quantity()))
                .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order(
                request.productId(),
                request.quantity(),
                amount);

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getAmount());

        orderEventProducer.publishOrderCreated(event);

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Order not found: " + orderId));

        return OrderResponse.from(order);
    }

    @Transactional
    public void markOrderCompleted(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(OrderStatus.COMPLETED);

        orderRepository.save(order);
    }

    @Transactional
    public void markOrderFailed(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(OrderStatus.FAILED);

        orderRepository.save(order);
    }
}
