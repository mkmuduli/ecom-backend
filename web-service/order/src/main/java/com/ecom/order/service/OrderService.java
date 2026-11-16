package com.ecom.order.service;

import com.ecom.order.dto.CreateOrderRequest;
import com.ecom.order.dto.OrderResponse;
import com.ecom.order.entity.Order;
import com.ecom.order.entity.OrderStatus;
import com.ecom.events.OrderCreatedEvent;
import com.ecom.order.messaging.OrderEventProducer;
import com.ecom.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderService(
            OrderRepository orderRepository,
            OrderEventProducer orderEventProducer) {

        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        Order order = new Order(
                request.productId(),
                request.quantity(),
                request.amount());

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
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
