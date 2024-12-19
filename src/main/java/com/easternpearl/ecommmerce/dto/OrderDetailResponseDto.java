package com.easternpearl.ecommmerce.dto;

import lombok.Data;

@Data
public class OrderDetailResponseDto {
    private ProductDTO product;
    private Integer quantity;
    private UserDTO seller;
}
