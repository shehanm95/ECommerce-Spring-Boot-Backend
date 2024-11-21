package com.easternpearl.ecommmerce.orders.repo;

import com.easternpearl.ecommmerce.orders.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByBuyerId(Integer buyerId);
}

