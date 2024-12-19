package com.easternpearl.ecommmerce.controller;


import com.easternpearl.ecommmerce.entity.OrderDetail;
import com.easternpearl.ecommmerce.dto.OrderRequestDto;
import com.easternpearl.ecommmerce.dto.OrderResponseDto;
import com.easternpearl.ecommmerce.dto.SellerOrderResponseDto;
import com.easternpearl.ecommmerce.service.impl.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutOrder(@RequestBody OrderRequestDto orderRequest
    ) {

        logger.info("order reciverd ========" + orderRequest);
        try {
            OrderResponseDto response = orderService.processOrder(orderRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/buyer/{userId}")
    public List<OrderResponseDto> getOrdersByBuyer(@PathVariable Long userId) {
        return orderService.getOrdersByBuyerId(userId);
    }


    @GetMapping("/seller/{sellerId}")
    public List<SellerOrderResponseDto> getSellerOrdersOnSellerId(@PathVariable Long sellerId){
        return orderService.getSellerOrdersOnSellerId(sellerId);
    }


    /**
     * Get all order details for a specific order ID.
     *
     * @param orderId the ID of the order
     * @return a list of order details for the specified order
     */
    @GetMapping("/details/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(orderId);
        return new ResponseEntity<>(orderDetails, HttpStatus.OK);
    }

}
