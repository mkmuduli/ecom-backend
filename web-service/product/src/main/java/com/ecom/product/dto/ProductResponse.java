package com.ecom.product.dto;

import java.math.BigDecimal;

import com.ecom.product.entity.Product;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Boolean active) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getActive());
    }
}
