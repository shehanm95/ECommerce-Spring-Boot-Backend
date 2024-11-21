package com.easternpearl.ecommmerce.orders.dto;

import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
import lombok.Data;

@Data
public class OrderDetailResponseDto {
    private ProductForBuyerDTO product;
    private Integer quantity;
}

