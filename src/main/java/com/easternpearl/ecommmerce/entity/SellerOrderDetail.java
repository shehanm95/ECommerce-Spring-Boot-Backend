package com.easternpearl.ecommmerce.entity;

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
    @Column(name = "seller_order_detail_id")
    private Long sellerOrderDetailId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "seller_order_id")
    private SellerOrder sellerOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
    private Integer quantity;
}
