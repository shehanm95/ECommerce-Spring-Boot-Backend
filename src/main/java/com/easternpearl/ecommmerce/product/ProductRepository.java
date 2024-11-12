package com.easternpearl.ecommmerce.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
}