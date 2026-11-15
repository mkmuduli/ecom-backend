package com.ecom.inventory.controller;

import com.ecom.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory/{productId}")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity) {

        inventoryService.reserveStock(productId, quantity);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseStock(
            @PathVariable("productId") Long productId,
            @RequestParam("quantity") int quantity) {

        inventoryService.releaseStock(productId, quantity);

        return ResponseEntity.ok().build();
    }
}
