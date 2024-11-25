package com.easternpearl.ecommmerce.product.repo;

import com.easternpearl.ecommmerce.product.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategory(Integer category);
    List<ProductEntity> findByProductNameContainingIgnoreCase(String productName);

    @Query("SELECT p FROM Product p WHERE p.isNew = true")
    List<ProductEntity> getNewProducts();
}
