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
import com.easternpearl.ecommmerce.user.rpo.UserRepository;
import com.easternpearl.ecommmerce.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {


    private final UserService userService;
    private final ProductService productService;
    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final SellerOrderDetailRepository sellerOrderDetailRepository;
    private final ObjectMapper mapper;
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    UserRepository userRepository;


    @Transactional
    public OrderResponseDto processOrder(OrderRequestDto orderRequest) {
        logger.info("$$$===================== Order Processing ======================$$$");
        userRepository.findById(orderRequest.getBuyerId()).ifPresent(
                orderRequest::setBuyer
        );
        List<SellerOrder> sellerOrders = new ArrayList<SellerOrder>();

        // Step 1: Validate Buyer
        UserDTO buyer = userService.findById(orderRequest.getBuyer().getId())
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
            orderDetail.setSellerId(productEntity.getSeller().getId());
            orderDetail.setProductEntity(productEntity);
            orderDetail.setQuantity(productOrder.getQuantity());
            orderDetail.setPrice(productEntity.getPrice());
            orderDetails.add(orderDetail);
            logger.info("************************ STARTED SETTING SELLER ORDER ************");

            // setting seller order
            boolean addedInLoop = false;
            for (int i = 0; i < sellerOrders.size(); i++) {
                SellerOrder sellerOrder = sellerOrders.get(i);
                if (sellerOrder.getSeller().getId().equals(productEntity.getSeller().getId())) {
                    SellerOrderDetail sellerOrderDetail = new SellerOrderDetail(
                            0L,
                            sellerOrder,
                            productEntity,
                            productOrder.getQuantity()
                    );
                    sellerOrder.getSellerOrderDetails().add(sellerOrderDetail);
                    addedInLoop = true;
                    logger.info("detail added inside the loop =======>>>>>");
                }
            }
            if(!addedInLoop){

                    SellerOrder newSellerOrder = new SellerOrder();
                    newSellerOrder.setSeller(productEntity.getSeller());
                    newSellerOrder.setBuyer(orderRequest.getBuyer());
                    newSellerOrder.setReceivedDate(LocalDateTime.now());
                    newSellerOrder.setOrderStatus( OrderStatus.Pending);
                newSellerOrder.setOrderAmount(productEntity.getPrice() * productOrder.getQuantity());
                newSellerOrder.setTotalProducts(productOrder.getQuantity());
                    newSellerOrder.setSellerOrderDetails(new ArrayList<SellerOrderDetail>());

                    SellerOrderDetail sellerOrderDetail = new SellerOrderDetail(
                            0L,
                            newSellerOrder,
                            productEntity,
                            productOrder.getQuantity()
                    );
                    newSellerOrder.getSellerOrderDetails().add(sellerOrderDetail);
                    sellerOrders.add(newSellerOrder);
                logger.info("detail added outside the loop =======>>>>>");

            }

            logger.info("************************ FiniSHED SETTING SELLER ORDER ************");
            // Calculate total amount
            totalAmount += productEntity.getPrice() * productOrder.getQuantity();
        }

        // Step 4: Save Order and OrderDetails
        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails);
        Orders savedOrder = ordersRepository.save(order); // saving order;
        orderDetailRepository.saveAll(orderDetails);
        logger.info("$$$===================== buyer order saved ======================$$$");

        //Step 5:save seller orders

        for (int i = 0; i < sellerOrders.size(); i++) {
            SellerOrder sellerOrder = sellerOrders.get(i);

            int productCount = 0;
            double orderAmount = 0.0;

            for (SellerOrderDetail detail : sellerOrder.getSellerOrderDetails()) {
                productCount += detail.getQuantity();
                orderAmount += detail.getQuantity() * detail.getProductEntity().getPrice();
            }

            sellerOrder.setOrderAmount(orderAmount);
            sellerOrder.setTotalProducts(productCount);

            sellerOrder.setBuyerOrder(savedOrder);
            SellerOrder savedSellerOrder = new SellerOrder();
            try {
                savedSellerOrder = sellerOrderRepository.save(sellerOrder);
                logger.info("seller order saved correctly ==============|||");
            } catch (Exception e) {
                logger.error("Error saving SellerOrder: {}", e.getMessage());

            }
            try {
                sellerOrderDetailRepository.saveAll(sellerOrder.getSellerOrderDetails());

            }catch (Exception e){
                logger.error("=XXXXXXXXXXXXXXX Error in saving details : {}", e.getMessage());
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
                        .getSellerNameAndImage(order.getSeller().getId()));

                for(SellerOrderDetail detail : order.getSellerOrderDetails()){
                    SellerOrderDetailResponseDto resDetail = mapper
                            .convertValue(detail,SellerOrderDetailResponseDto.class);

                    resDetail.setProduct(mapper.convertValue(detail.getProductEntity(),ProductForBuyerDTO.class));

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
