package com.easternpearl.ecommmerce.orders.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerOrderDetailResponseDto {

    private Long sellerOrderDetailId;
    private Long productId;
    private Integer quantity;

}
