package com.easternpearl.ecommmerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerStaticsDto {

    private Long allOrdersCount;
    private Long boughtProductCount;
    private Double totalSpent;
}
