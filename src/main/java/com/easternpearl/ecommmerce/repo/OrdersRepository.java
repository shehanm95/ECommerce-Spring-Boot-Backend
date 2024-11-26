package com.easternpearl.ecommmerce.repo;

import com.easternpearl.ecommmerce.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByBuyerId(Long buyerId);
}

