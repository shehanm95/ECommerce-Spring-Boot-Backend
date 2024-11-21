package com.easternpearl.ecommmerce.orders.dto;

import lombok.Data;

@Data
public class OrderDetaiReqlDto {
    private Long productId;
    private Integer sellerId;
    private Integer quantity;
}

