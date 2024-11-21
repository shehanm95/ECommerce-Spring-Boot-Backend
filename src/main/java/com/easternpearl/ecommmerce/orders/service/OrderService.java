package com.easternpearl.ecommmerce.orders.service;

import com.easternpearl.ecommmerce.orders.Entity.OrderDetail;
import com.easternpearl.ecommmerce.orders.Entity.Orders;
import com.easternpearl.ecommmerce.orders.Entity.SellerOrder;
import com.easternpearl.ecommmerce.orders.Entity.SellerOrderDetail;
import com.easternpearl.ecommmerce.orders.Entity.enums.OrderStatus;
import com.easternpearl.ecommmerce.orders.dto.OrderDetaiReqlDto;
import com.easternpearl.ecommmerce.orders.dto.OrderDetailResponseDto;
import com.easternpearl.ecommmerce.orders.dto.OrderRequestDto;
import com.easternpearl.ecommmerce.orders.dto.OrderResponseDto;
import com.easternpearl.ecommmerce.orders.repo.OrderDetailRepository;
import com.easternpearl.ecommmerce.orders.repo.OrdersRepository;
import com.easternpearl.ecommmerce.product.Product;
import com.easternpearl.ecommmerce.product.ProductService;
import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
import com.easternpearl.ecommmerce.user.DTO.UserDTO;
import com.easternpearl.ecommmerce.user.entity.UserEntity;
import com.easternpearl.ecommmerce.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final ProductService productService;
    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final SellerOrderDetailRepository sellerOrderDetailRepository;
    private final ObjectMapper mapper;


    @Transactional
    public OrderResponseDto processOrder(OrderRequestDto orderRequest) {
        System.out.println("$$$===================== Order Processing ======================$$$");
        List<SellerOrder> sellerOrders = new ArrayList<SellerOrder>();

        // Step 1: Validate Buyer
        UserDTO buyer = userService.findById(orderRequest.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Buyer ID to process order========"));

        // Step 2: Initialize the Order
        Orders order = new Orders();
        order.setBuyer(mapper.convertValue(buyer , UserEntity.class));
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0);

        // Step 3: Create OrderDetails and Deduct Stock
        double totalAmount = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderDetaiReqlDto productOrder : orderRequest.getOrderDetails()) {
            Product product = productService.findById(productOrder.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));

            // Check stock availability
            if (product.getProductCount() < productOrder.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: " + product.getProductName());
            }

            // Deduct stock
            product.setProductCount(product.getProductCount() - productOrder.getQuantity());
            productService.save(product);

            // Create and add OrderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setSellerId(product.getSellerId());
            orderDetail.setProduct(product);
            orderDetail.setQuantity(productOrder.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetails.add(orderDetail);
            System.out.println("************************ STARTED SETTING SELLER ORDER ************");
            // setting seller order
            boolean addedInLoop = false;
            for (int i = 0; i < sellerOrders.size(); i++) {
                SellerOrder sellerOrder = sellerOrders.get(i);
                if (sellerOrder.getSellerId().equals(product.getSellerId())) {
                    SellerOrderDetail sellerOrderDetail = new SellerOrderDetail(
                            0L,
                            sellerOrder,
                            product.getId(),
                            productOrder.getQuantity()
                    );
                    sellerOrder.getSellerOrderDetails().add(sellerOrderDetail);
                    addedInLoop = true;
                    System.out.println("detail added inside the loop =======>>>>>");
                }
            }
            if(!addedInLoop){

                    SellerOrder newSellerOrder = new SellerOrder();
                    newSellerOrder.setSellerId(product.getSellerId());
                    newSellerOrder.setBuyerId(orderRequest.getBuyerId());
                    newSellerOrder.setReceivedDate(LocalDateTime.now());
                    newSellerOrder.setOrderStatus( OrderStatus.Pending);
                newSellerOrder.setOrderAmount(product.getPrice() * productOrder.getQuantity());
                newSellerOrder.setTotalProducts(productOrder.getQuantity());
                    newSellerOrder.setSellerOrderDetails(new ArrayList<SellerOrderDetail>());

                    SellerOrderDetail sellerOrderDetail = new SellerOrderDetail(
                            0L,
                            newSellerOrder,
                            product.getId(),
                            productOrder.getQuantity()
                    );
                    newSellerOrder.getSellerOrderDetails().add(sellerOrderDetail);
                    sellerOrders.add(newSellerOrder);
                    System.out.println("detail added outside the loop =======>>>>>");

            }

            System.out.println("************************ FiniSHED SETTING SELLER ORDER ************");
            // Calculate total amount
            totalAmount += product.getPrice() * productOrder.getQuantity();
        }

        // Step 4: Save Order and OrderDetails
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        Orders savedOrder = ordersRepository.save(order); // saving order;
        orderDetailRepository.saveAll(orderDetails);
            System.out.println("$$$===================== buyer order saved ======================$$$");

        //Step 5:save seller orders

        for (int i = 0; i < sellerOrders.size(); i++) {
            SellerOrder sellerOrder = sellerOrders.get(i);

            int productCount = 0;
            double orderAmount = 0.0;

            for (SellerOrderDetail detail : sellerOrder.getSellerOrderDetails()) {
                productCount += detail.getQuantity();
                Product product = productService.findById(detail.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));
                orderAmount += detail.getQuantity() * product.getPrice();
            }

            sellerOrder.setOrderAmount(orderAmount);
            sellerOrder.setTotalProducts(productCount);

            sellerOrder.setBuyerOrder(savedOrder);
            SellerOrder savedSellerOrder = new SellerOrder();
            try {
                savedSellerOrder = sellerOrderRepository.save(sellerOrder);
                System.out.println("seller order saved correctly ==============|||");
            } catch (Exception e) {
                System.err.println("Error saving SellerOrder: " + e.getMessage());

            }
            try {
                sellerOrderDetailRepository.saveAll(sellerOrder.getSellerOrderDetails());

            }catch (Exception e){
                System.out.println("=XXXXXXXXXXXXXXX Error in saving details : "+ e.getMessage());
            }
       }

        // Step 6: Map to Response DTO
        List<OrderDetailResponseDto> orderDetailResponseDtos = orderDetails.stream()
                .map(detail -> {
                    ProductForBuyerDTO productDto = mapper.convertValue(detail.getProduct(), ProductForBuyerDTO.class);
                    OrderDetailResponseDto responseDto = new OrderDetailResponseDto();
                    responseDto.setProduct(productDto);
                    responseDto.setQuantity(detail.getQuantity());
                    return responseDto;
                })
                .toList();

        return new OrderResponseDto(
                order.getId(),
                totalAmount,
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                savedOrder.getEndDate(),
                orderDetailResponseDtos
        );

    }

    public List<SellerOrder> getSellerOrdersOnSellerId(Integer sellerId){
        return sellerOrderRepository.findAllBySellerId(sellerId);
    }


    public List<OrderResponseDto> getOrdersByBuyerId(Integer buyerId) {
        List<Orders> orders = ordersRepository.findByBuyerId(buyerId);

        return orders.stream().map(order -> {
            OrderResponseDto orderResponse = new OrderResponseDto();
            orderResponse.setOrderId(order.getId());
            orderResponse.setTotalAmount(order.getTotalAmount());
            orderResponse.setStatus(order.getStatus()); // Include order status
            orderResponse.setOrderDate(order.getOrderDate()); // Include order date

            // Map OrderDetails to OrderDetailResponseDto
            List<OrderDetailResponseDto> orderDetailDtos = order.getOrderDetails().stream()
                    .map(detail -> {
                        OrderDetailResponseDto detailDto = new OrderDetailResponseDto();
                        detailDto.setProduct(mapper.convertValue(detail.getProduct(), ProductForBuyerDTO.class));
                        detailDto.setQuantity(detail.getQuantity());
                        return detailDto;
                    })
                    .toList();

            orderResponse.setOrderDetails(orderDetailDtos);
            return orderResponse;
        }).toList();
    }
}
