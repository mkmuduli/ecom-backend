package com.ecom.order.entity;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}
