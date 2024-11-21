package com.easternpearl.ecommmerce.orders.service;

import com.easternpearl.ecommmerce.orders.Entity.SellerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerOrderRepository extends JpaRepository<SellerOrder , Long> {
    List<SellerOrder> findAllBySellerId(Integer sellerId);
}
