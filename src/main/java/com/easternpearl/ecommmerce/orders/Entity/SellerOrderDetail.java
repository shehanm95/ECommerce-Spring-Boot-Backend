package com.easternpearl.ecommmerce.orders.Entity;

import com.easternpearl.ecommmerce.product.model.ProductEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private SellerOrder sellerOrder;
    @ManyToMany
    @JoinTable(
            name = "product_seller_order_detail",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "seller_order_detail_id")
    )
    private ProductEntity productEntity;
    private Integer quantity;
}
