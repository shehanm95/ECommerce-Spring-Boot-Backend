package com.easternpearl.ecommmerce.orders.dto;


import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
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
