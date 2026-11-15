package com.ecom.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, int requested) {
        super(
                "Insufficient stock for product: "
                        + productId
                        + ". Requested: "
                        + requested);
    }
}
