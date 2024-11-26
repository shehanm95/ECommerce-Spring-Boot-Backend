package com.easternpearl.ecommmerce.entity;

import com.easternpearl.ecommmerce.enums.OrderStatus;
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


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "seller_id")
    private UserEntity seller;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "buyer_id")
    private UserEntity buyer;
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