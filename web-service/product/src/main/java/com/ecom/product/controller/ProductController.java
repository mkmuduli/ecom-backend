package com.ecom.product.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecom.product.model.Product;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @GetMapping({ "", "/" })
    public String getProduct() {
        return "all products";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable("id") Long id) {
        return "Product " + id;
    }

    @PostMapping({ "", "/" })
    public String createProduct(@Valid @RequestBody Product product) {
        return "Product created";
    }

    @PutMapping("/{id}")
    public String updateProduct(@PathVariable("id") Long id, @RequestBody String entity) {
        return entity;
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        return "Product deleted";
    }

}
