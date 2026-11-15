package com.ecom.inventory.controller;

import com.ecom.inventory.service.InventoryService;
import com.ecom.inventory.entity.Inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.createInventory(
                request.productId(), request.availableQuantity()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Void> reserveStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity) {

        inventoryService.reserveStock(productId, quantity);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<Void> releaseStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity) {

        inventoryService.releaseStock(productId, quantity);

        return ResponseEntity.ok().build();
    }

    public record InventoryRequest(Long productId, Integer availableQuantity) {}
}
