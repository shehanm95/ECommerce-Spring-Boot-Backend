package com.easternpearl.ecommmerce.dto;

import lombok.Data;

@Data
public class OrderDetailResponseDto {
    private ProductForBuyerDTO product;
    private Integer quantity;
    private Long sellerId;
}

