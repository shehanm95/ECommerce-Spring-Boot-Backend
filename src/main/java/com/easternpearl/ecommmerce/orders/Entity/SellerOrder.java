package com.easternpearl.ecommmerce.orders.Entity;

import com.easternpearl.ecommmerce.orders.Entity.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "seller_orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_order_id")
    private Long sellerOrderId;


    private Integer sellerId;


    private Integer buyerId;
    private Double orderAmount;
    private Integer totalProducts;


    private LocalDateTime receivedDate;

    private LocalDateTime finishedDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // "Pending", "Shipped", "Delivered"


    @OneToMany(mappedBy = "sellerOrder")
    @JsonIgnore
    private List<SellerOrderDetail> sellerOrderDetails;

    @ManyToOne
    @JoinColumn(name = "buyer_order_id")
    @JsonIgnore
    private Orders buyerOrder;
}