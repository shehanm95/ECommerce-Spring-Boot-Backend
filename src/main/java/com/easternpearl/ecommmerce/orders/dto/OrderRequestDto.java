package com.easternpearl.ecommmerce.orders.dto;

import com.easternpearl.ecommmerce.user.DTO.UserDTO;
import com.easternpearl.ecommmerce.user.entity.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Integer buyerId;
    private UserEntity buyer;
    private List<OrderDetailReqlDto> orderDetails;
}

