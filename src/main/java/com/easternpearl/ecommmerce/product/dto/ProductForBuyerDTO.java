package com.easternpearl.ecommmerce.product.dto;

import com.easternpearl.ecommmerce.product.model.enums.ProductState;
import com.easternpearl.ecommmerce.user.DTO.UserNameAndImg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductForBuyerDTO {
    private Long id;
    private String productName;
    private Double price;
    private Integer category;
    private Integer subCategory;
    private String productImageLink;
    private Integer sellerId;
    private Double rate;
    private Integer rateCount;
    private ProductState productState;
    private Integer productCount;
    private String productCode;
    private Boolean isNew;

    private UserNameAndImg sellerDetails;
}
