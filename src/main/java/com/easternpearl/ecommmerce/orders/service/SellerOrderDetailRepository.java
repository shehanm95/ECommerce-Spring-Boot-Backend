package com.easternpearl.ecommmerce.orders.service;

import com.easternpearl.ecommmerce.orders.Entity.SellerOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerOrderDetailRepository extends JpaRepository<SellerOrderDetail, Long> {
}