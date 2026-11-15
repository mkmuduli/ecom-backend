package com.ecom.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.product.dto.CreateProductRequest;
import com.ecom.product.dto.ProductResponse;
import com.ecom.product.entity.Product;
import com.ecom.product.exception.ProductNotFoundException;
import com.ecom.product.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product(
                request.name(),
                request.price(),
                request.stock());

        Product saved = productRepository.save(product);

        return ProductResponse.from(saved);
    }

    public ProductResponse getByProductId(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return ProductResponse.from(product);
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse updateProduct(Long id, CreateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());

        Product updated = productRepository.save(product);

        return ProductResponse.from(updated);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
    }
}
