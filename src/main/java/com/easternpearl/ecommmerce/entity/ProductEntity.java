package com.easternpearl.ecommmerce.entity;

import com.easternpearl.ecommmerce.enums.ProductState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ManyToOne
    //@JsonIgnore
    @JoinColumn(name = "seller_id")
    private UserEntity seller;

    private Double rate;

    private Integer rateCount;

    @Enumerated(EnumType.STRING)
    private ProductState productState;

    private Integer productCount;

    private String productCode;

    private Boolean isNew;

    @JsonIgnore
    @ManyToMany(mappedBy = "productEntity" , cascade = CascadeType.REMOVE)
    private List<SellerOrderDetail> sellerOrderDetail;

}