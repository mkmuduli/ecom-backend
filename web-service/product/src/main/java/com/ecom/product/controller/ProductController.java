package com.ecom.product.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ecom.product.dto.CreateProductRequest;
import com.ecom.product.dto.ProductResponse;
import com.ecom.product.entity.Product;
import com.ecom.product.service.ProductService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({ "", "/" })
    public List<ProductResponse> getProduct() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable("id") Long id) {
        return productService.getByProductId(id);
    }

    @PostMapping({ "", "/" })
    public String createProduct(@Valid @RequestBody CreateProductRequest product) {
        return "Product created";
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable("id") Long id,
            @Valid @RequestBody CreateProductRequest product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.delete(id);
    }

}
