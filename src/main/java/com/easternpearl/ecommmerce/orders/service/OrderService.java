package com.easternpearl.ecommmerce.orders.service;

import com.easternpearl.ecommmerce.orders.Entity.OrderDetail;
import com.easternpearl.ecommmerce.orders.Entity.Orders;
import com.easternpearl.ecommmerce.orders.Entity.SellerOrder;
import com.easternpearl.ecommmerce.orders.Entity.SellerOrderDetail;
import com.easternpearl.ecommmerce.orders.Entity.enums.OrderStatus;
import com.easternpearl.ecommmerce.orders.dto.*;
import com.easternpearl.ecommmerce.orders.repo.OrderDetailRepository;
import com.easternpearl.ecommmerce.orders.repo.OrdersRepository;
import com.easternpearl.ecommmerce.product.ProductService;
import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
import com.easternpearl.ecommmerce.product.model.ProductEntity;
import com.easternpearl.ecommmerce.product.repo.ProductRepository;
import com.easternpearl.ecommmerce.user.DTO.UserDTO;
import com.easternpearl.ecommmerce.user.entity.UserEntity;
import com.easternpearl.ecommmerce.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private final ProductRepository productRepository;


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
        order.setStatus(OrderStatus.Pending);
        order.setTotalAmount(0.0);

        // Step 3: Create OrderDetails and Deduct Stock
        double totalAmount = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderDetailReqlDto productOrder : orderRequest.getOrderDetails()) {
            ProductEntity productEntity = productService.findById(productOrder.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));

            // Check stock availability
            if (productEntity.getProductCount() < productOrder.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: " + productEntity.getProductName());
            }

            // Deduct stock
            productEntity.setProductCount(productEntity.getProductCount() - productOrder.getQuantity());
            productService.save(productEntity);

            // Create and add OrderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setSellerId(productEntity.getSellerId());
            orderDetail.setProductEntity(productEntity);
            orderDetail.setQuantity(productOrder.getQuantity());
            orderDetail.setPrice(productEntity.getPrice());
            orderDetails.add(orderDetail);
            System.out.println("************************ STARTED SETTING SELLER ORDER ************");
            // setting seller order
            boolean addedInLoop = false;
            for (int i = 0; i < sellerOrders.size(); i++) {
                SellerOrder sellerOrder = sellerOrders.get(i);
                if (sellerOrder.getSellerId().equals(productEntity.getSellerId())) {
                    SellerOrderDetail sellerOrderDetail = new SellerOrderDetail(
                            0L,
                            sellerOrder,
                            productEntity.getId(),
                            productOrder.getQuantity()
                    );
                    sellerOrder.getSellerOrderDetails().add(sellerOrderDetail);
                    addedInLoop = true;
                    System.out.println("detail added inside the loop =======>>>>>");
                }
            }
            if(!addedInLoop){

                    SellerOrder newSellerOrder = new SellerOrder();
                    newSellerOrder.setSellerId(productEntity.getSellerId());
                    newSellerOrder.setBuyerId(orderRequest.getBuyerId());
                    newSellerOrder.setReceivedDate(LocalDateTime.now());
                    newSellerOrder.setOrderStatus( OrderStatus.Pending);
                newSellerOrder.setOrderAmount(productEntity.getPrice() * productOrder.getQuantity());
                newSellerOrder.setTotalProducts(productOrder.getQuantity());
                    newSellerOrder.setSellerOrderDetails(new ArrayList<SellerOrderDetail>());

                    SellerOrderDetail sellerOrderDetail = new SellerOrderDetail(
                            0L,
                            newSellerOrder,
                            productEntity.getId(),
                            productOrder.getQuantity()
                    );
                    newSellerOrder.getSellerOrderDetails().add(sellerOrderDetail);
                    sellerOrders.add(newSellerOrder);
                    System.out.println("detail added outside the loop =======>>>>>");

            }

            System.out.println("************************ FiniSHED SETTING SELLER ORDER ************");
            // Calculate total amount
            totalAmount += productEntity.getPrice() * productOrder.getQuantity();
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
                ProductEntity productEntity = productService.findById(detail.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Product ID"));
                orderAmount += detail.getQuantity() * productEntity.getPrice();
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
                    ProductForBuyerDTO productDto = mapper.convertValue(detail.getProductEntity(), ProductForBuyerDTO.class);
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

    public List<SellerOrderResponseDto> getSellerOrdersOnSellerId(Integer sellerId){
        return sellerOrderRepository.findAllBySellerId(sellerId).stream()
            .map(order ->{
                SellerOrderResponseDto sellerOrder = mapper
                        .convertValue(order,SellerOrderResponseDto.class);
                sellerOrder.setBuyerOrderId(order.getBuyerOrder().getId());
                sellerOrder.setUserNameAndImg(productService
                        .getSellerNameAndImage(order.getSellerId()));

                for(SellerOrderDetail detail : order.getSellerOrderDetails()){
                    SellerOrderDetailResponseDto resDetail = mapper
                            .convertValue(detail,SellerOrderDetailResponseDto.class);

                    resDetail.setProduct(mapper.convertValue(productRepository.findById(detail.getProductId()),ProductForBuyerDTO.class));

                    sellerOrder.getSellerOrderDetailsDto().add(resDetail);


                }
                //System.out.println("seller order ============: " + sellerOrder);
                return sellerOrder;
            }).toList();
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
                        detailDto.setSellerId(detail.getSellerId());
                        detailDto.setProduct(mapper.convertValue(detail.getProductEntity(), ProductForBuyerDTO.class));
                        detailDto.setQuantity(detail.getQuantity());
                        detailDto.getProduct().setSellerDetails(productService.getSellerNameAndImage(detail.getSellerId()));
                        System.out.println(detailDto);
                        return detailDto;
                    })
                    .toList();
            orderResponse.setOrderDetails(orderDetailDtos);
            return orderResponse;
        }).toList();
    }


    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }


}
