package com.easternpearl.ecommmerce.dto;

import com.easternpearl.ecommmerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime endDate;
    private List<OrderDetailResponseDto> orderDetails;
}
