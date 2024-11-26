package com.easternpearl.ecommmerce.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerOrderDetailResponseDto {

    private Long sellerOrderDetailId;
    private ProductForBuyerDTO product;
    private Integer quantity;

}
