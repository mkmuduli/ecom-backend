package com.ecom.inventory.service;

import com.ecom.inventory.exception.InsufficientStockException;
import com.ecom.inventory.exception.InventoryNotFoundException;
import com.ecom.inventory.messaging.InventoryEventProducer;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.entity.ProcessedEvent;
import com.ecom.events.InventoryFailedEvent;
import com.ecom.events.InventoryReservedEvent;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.repository.ProcessedEventRepository;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

        private final InventoryRepository inventoryRepository;
        private final InventoryEventProducer inventoryEventProducer;
        private final ProcessedEventRepository processedEventRepository;

        public InventoryService(
                        InventoryRepository inventoryRepository,
                        ProcessedEventRepository processedEventRepository,
                        InventoryEventProducer inventoryEventProducer) {

                this.inventoryRepository = inventoryRepository;
                this.processedEventRepository = processedEventRepository;
                this.inventoryEventProducer = inventoryEventProducer;
        }

        @Transactional
        public Inventory createInventory(Long productId, Integer availableQuantity) {
                if (productId == null || availableQuantity == null || availableQuantity < 0) {
                        throw new IllegalArgumentException(
                                        "productId and a non-negative availableQuantity are required");
                }
                // Create the row on first use; subsequent calls set available stock
                // without disturbing quantities already reserved for orders.
                inventoryRepository.insertIfAbsent(productId, availableQuantity);
                return inventoryRepository.findByProductId(productId)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Inventory insert did not create or find a row for product: " + productId));
        }

        @Transactional(readOnly = true)
        public Inventory getInventory(Long productId) {
                return inventoryRepository.findByProductId(productId)
                                .orElseThrow(() -> new InventoryNotFoundException(productId));
        }

        public void reserveStockFromEvent(
                        String eventId,
                        Long orderId,
                        Long productId,
                        int quantity,
                        BigDecimal amount) {

                // 1. Check idempotency
                if (processedEventRepository.existsByEventId(eventId)) {
                        System.out.println("Duplicate event ignored: " + eventId);
                        return;
                }

                // 2. Call existing method
                reserveStock(orderId, productId, quantity, amount);

                // 3. Record event as processed
                processedEventRepository.save(
                                new ProcessedEvent(eventId));
        }

        @Transactional
        public void reserveStock(
                        Long orderId,
                        Long productId,
                        int quantity,
                        BigDecimal amount) {

                Inventory inventory = inventoryRepository
                                .findByProductId(productId)
                                .orElseThrow(() -> new InventoryNotFoundException(productId));

                if (inventory.getAvailableQuantity() < quantity) {

                        inventoryEventProducer.publishFailed(
                                        new InventoryFailedEvent(
                                                        orderId,
                                                        productId,
                                                        quantity,
                                                        "Insufficient stock"));

                        throw new InsufficientStockException(
                                        productId,
                                        quantity);
                }

                inventory.setAvailableQuantity(
                                inventory.getAvailableQuantity() - quantity);

                inventory.setReservedQuantity(
                                inventory.getReservedQuantity() + quantity);

                inventoryRepository.save(inventory);

                inventoryEventProducer.publishReserved(
                                new InventoryReservedEvent(
                                                orderId,
                                                productId,
                                                quantity,
                                                amount));
        }

        @Transactional
        public void releaseStock(Long productId, int quantity) {

                Inventory inventory = inventoryRepository
                                .findByProductId(productId)
                                .orElseThrow(() -> new InventoryNotFoundException(productId));

                inventory.setReservedQuantity(
                                inventory.getReservedQuantity() - quantity);

                inventory.setAvailableQuantity(
                                inventory.getAvailableQuantity() + quantity);

                inventoryRepository.save(inventory);
        }
}
