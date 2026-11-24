package com.ecom.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required") String name,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.001", message = "Price must be greater than 0") BigDecimal price

) {
}
