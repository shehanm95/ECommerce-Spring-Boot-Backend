package com.easternpearl.ecommmerce.orders.repo;

import com.easternpearl.ecommmerce.orders.Entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
