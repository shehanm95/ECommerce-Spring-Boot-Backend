package com.easternpearl.ecommmerce.orders.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Integer buyerId;
    private List<OrderDetaiReqlDto> orderDetails;
}

