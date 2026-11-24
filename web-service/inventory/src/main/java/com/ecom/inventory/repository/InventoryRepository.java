package com.ecom.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecom.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Modifying
    @Query(value = """
            INSERT INTO inventory (product_id, available_quantity, reserved_quantity)
            VALUES (:productId, :availableQuantity, 0)
            ON CONFLICT (product_id) DO UPDATE
            SET available_quantity = EXCLUDED.available_quantity
            """, nativeQuery = true)
    void insertIfAbsent(
            @Param("productId") Long productId,
            @Param("availableQuantity") Integer availableQuantity);

}
