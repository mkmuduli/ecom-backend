package com.ecom.inventory.service;

import com.ecom.inventory.exception.InsufficientStockException;
import com.ecom.inventory.exception.InventoryNotFoundException;
import com.ecom.inventory.messaging.InventoryEventProducer;
import com.ecom.inventory.entity.Inventory;
import com.ecom.inventory.event.InventoryFailedEvent;
import com.ecom.inventory.event.InventoryReservedEvent;
import com.ecom.inventory.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    public InventoryService(InventoryRepository inventoryRepository, InventoryEventProducer inventoryEventProducer) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    @Transactional
    public Inventory createInventory(Long productId, Integer availableQuantity) {
        if (productId == null || availableQuantity == null || availableQuantity < 0) {
            throw new IllegalArgumentException("productId and a non-negative availableQuantity are required");
        }
        return inventoryRepository.save(new Inventory(productId, availableQuantity));
    }

    @Transactional(readOnly = true)
    public Inventory getInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    @Transactional
    public void reserveStock(
            Long orderId,
            Long productId,
            int quantity) {

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
                        quantity));
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
