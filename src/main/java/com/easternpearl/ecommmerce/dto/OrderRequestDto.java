package com.easternpearl.ecommmerce.dto;

import com.easternpearl.ecommmerce.entity.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Long buyerId;
    private UserEntity buyer;
    private List<OrderDetailReqlDto> orderDetails;
}

