package com.easternpearl.ecommmerce.product.model;

import com.easternpearl.ecommmerce.orders.Entity.SellerOrderDetail;
import com.easternpearl.ecommmerce.product.model.enums.ProductState;
import com.easternpearl.ecommmerce.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "product_name", nullable = false)
    private String productName; // Product name

    @Column(name = "price", nullable = false)
    private Double price; // Product price

    @Column(name = "category", nullable = false)
    private Integer category; // Product category

    @Column(name = "sub_category", nullable = false)
    private Integer subCategory; // Product category

    @Column(name = "product_image_link", nullable = false)
    private String productImageLink; // Product image URL

    @ManyToMany
    private UserEntity seller;

    private Double rate;

    private Integer rateCount;

    @Enumerated(EnumType.STRING)

    private ProductState productState;

    private Integer productCount;

    private String productCode;

    private Boolean isNew;

    @JsonIgnore
    @ManyToMany(mappedBy = "product")
    private SellerOrderDetail sellerOrderDetail;

}