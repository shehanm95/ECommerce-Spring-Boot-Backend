package com.easternpearl.ecommmerce.orders.dto;

import com.easternpearl.ecommmerce.orders.Entity.enums.OrderStatus;
import com.easternpearl.ecommmerce.user.DTO.UserNameAndImg;
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
    private Integer sellerId;
    private Integer buyerId;
    private Double orderAmount;
    private Integer totalProducts;
    private LocalDateTime receivedDate;
    private LocalDateTime finishedDate;
    private OrderStatus orderStatus; //Pending, Shipped, Canceled, Completed
    private List<SellerOrderDetailResponseDto> sellerOrderDetailsDto = new ArrayList<>();
    private Long buyerOrderId;
    private UserNameAndImg userNameAndImg;
}

