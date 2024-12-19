package com.easternpearl.ecommmerce.dto;

import com.easternpearl.ecommmerce.entity.UserEntity;
import com.easternpearl.ecommmerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerOrderResponseDto {

    private Long sellerOrderId;
    private UserDTO seller;
    private UserDTO buyer;
    private Double orderAmount;
    private Integer totalProducts;
    private LocalDateTime receivedDate;
    private LocalDateTime finishedDate;
    private OrderStatus orderStatus; //Pending, Shipped, Canceled, Completed
    private List<SellerOrderDetailResponseDto> sellerOrderDetailsDto = new ArrayList<>();
    private Long buyerOrderId;
}

