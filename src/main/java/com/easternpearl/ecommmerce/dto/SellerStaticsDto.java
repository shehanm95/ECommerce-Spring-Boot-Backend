package com.easternpearl.ecommmerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerStaticsDto {
    private Long allOrdersCount;
    private Long soldProductCount;
    private Double totalRevenue;
}
