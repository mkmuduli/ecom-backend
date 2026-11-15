package com.ecom.product.repository;

import com.ecom.product.entity.Product;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public class ProductRepository extends JpaRepository<Product, Long> {

}
