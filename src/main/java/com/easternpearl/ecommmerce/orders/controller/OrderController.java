package com.easternpearl.ecommmerce.orders.controller;


import com.easternpearl.ecommmerce.orders.Entity.SellerOrder;
import com.easternpearl.ecommmerce.orders.dto.OrderRequestDto;
import com.easternpearl.ecommmerce.orders.dto.OrderResponseDto;
import com.easternpearl.ecommmerce.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutOrder(@RequestBody OrderRequestDto orderRequest) {
        System.out.println("order reciverd ========" + orderRequest);
        try {
            OrderResponseDto response = orderService.processOrder(orderRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/buyer/{userId}")
    public List<OrderResponseDto> getOrdersByBuyer(@PathVariable Integer userId) {
        System.out.println("Fetching orders for buyer ID: " + userId);
        return orderService.getOrdersByBuyerId(userId);
    }


    @GetMapping("/seller/{sellerId}")
    public List<SellerOrder> getSellerOrdersOnSellerId(Integer sellerId){
        return orderService.getSellerOrdersOnSellerId(sellerId);
    }


}