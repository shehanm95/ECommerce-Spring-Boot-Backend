package com.easternpearl.ecommmerce.repo;

import com.easternpearl.ecommmerce.entity.SellerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerOrderRepository extends JpaRepository<SellerOrder , Long> {
    List<SellerOrder> findAllBySellerId(Long sellerId);
}
