package com.easternpearl.ecommmerce.orders.Entity;


import com.easternpearl.ecommmerce.orders.Entity.enums.OrderStatus;
import com.easternpearl.ecommmerce.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private UserEntity buyer; // The buyer who placed the order


    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // "Pending", "Shipped", "Delivered"

    private LocalDateTime endDate;
}
