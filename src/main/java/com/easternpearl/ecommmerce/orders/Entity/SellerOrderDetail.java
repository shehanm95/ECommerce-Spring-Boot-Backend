package com.easternpearl.ecommmerce.orders.Entity;

import com.easternpearl.ecommmerce.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seller_order_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerOrderDetailId;

    @ManyToOne
    @JoinColumn(name = "seller_order_id", nullable = false)
    private SellerOrder sellerOrder;


    private Long productId;

    private Integer quantity;
}
