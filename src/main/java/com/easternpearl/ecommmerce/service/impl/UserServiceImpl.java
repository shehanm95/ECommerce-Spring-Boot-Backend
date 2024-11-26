package com.easternpearl.ecommmerce.service.impl;

import com.easternpearl.ecommmerce.dto.OrderDetailResponseDto;
import com.easternpearl.ecommmerce.dto.OrderResponseDto;
import com.easternpearl.ecommmerce.dto.SellerOrderDetailResponseDto;
import com.easternpearl.ecommmerce.dto.SellerOrderResponseDto;
import com.easternpearl.ecommmerce.repo.SellerOrderDetailRepository;
import com.easternpearl.ecommmerce.repo.SellerOrderRepository;

import com.easternpearl.ecommmerce.service.ProductService;
import com.easternpearl.ecommmerce.dto.ProductForBuyerDTO;
import com.easternpearl.ecommmerce.repo.ProductRepository;
import com.easternpearl.ecommmerce.dto.LoginDto;
import com.easternpearl.ecommmerce.dto.RegisterDto;
import com.easternpearl.ecommmerce.dto.AdminStaticsDto;
import com.easternpearl.ecommmerce.dto.BuyerStaticsDto;
import com.easternpearl.ecommmerce.dto.SellerStaticsDto;
import com.easternpearl.ecommmerce.dto.UserDTO;
import com.easternpearl.ecommmerce.entity.UserEntity;
import com.easternpearl.ecommmerce.enums.UserRole;
import com.easternpearl.ecommmerce.enums.UserState;
import com.easternpearl.ecommmerce.repo.UserRepository;
import com.easternpearl.ecommmerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.util.UriUtils.extractFileExtension;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Lazy
    @Autowired
    private  UserRepository userRepository;
    @Lazy
    @Autowired
    private ObjectMapper mapper;
    @Lazy
    @Autowired
    private SellerOrderRepository sellerOrderRepository;
    @Lazy
    @Autowired
    private SellerOrderDetailRepository sellerOrderDetailRepository;
    @Lazy
    @Autowired
    private ProductService productService;
    @Lazy
    @Autowired
    private ProductRepository productRepository;
    @Lazy
    @Autowired
    private OrderService orderService;


    private final Path imagePath = Paths.get("src/main/resources/images/user");
    private void createDirectoryIfNotExists() {
        try {
            // Ensure the directory exists, if not, it will be created
            Files.createDirectories(imagePath);
            System.out.println("file path created========================");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + imagePath.toString(), e);
        }
    }


    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(u->mapper.convertValue(u,UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username).map(u->mapper.convertValue(u,UserDTO.class));
    }

    @Override
    public UserDTO login(LoginDto loginDto) {
        Optional<UserEntity> ue =  userRepository.findByUsername(loginDto.getUsername());
        System.out.println(ue);
        if(ue.isPresent()){
            String loginPassword = encryptPassword(loginDto.getPassword());
            if(ue.get().getPassword().equals(loginPassword)){
                return mapper.convertValue(ue,UserDTO.class);
            }
        }
        return null;
    }

    @Override
    public UserDTO register(RegisterDto registerDto, MultipartFile image) {
        UserEntity user = mapper.convertValue(registerDto, UserEntity.class);
        user.setPassword(encryptPassword(user.getPassword()));
        System.out.println(user.getPassword());
        createDirectoryIfNotExists();
        String imageName = saveImage(user.getUsername(),image);
        user.setImageLink("/users/image/" + imageName);
        user = userRepository.save(user);
        return mapper.convertValue(user,UserDTO.class);
    }

    @Override
    public List<UserDTO> findByRole(UserRole role) {
        return userRepository.
                findByUserRole(role)
                .stream()
                .map(u->mapper.convertValue(u,UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(u->mapper.convertValue(u,UserDTO.class));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setUserState(UserState.DELETED);
            userRepository.save(user);
        });
    }

    @Override
    public AdminStaticsDto getAdminStatics() {
        long allUsersCount = userRepository.count();
        long allOrdersCount = sellerOrderRepository.count();
        long soldProductCount = 0;
        double totalRevenue = 0;
        List<SellerOrderDetailResponseDto> details = sellerOrderDetailRepository.findAll().stream()
                .map(detail ->{
                            SellerOrderDetailResponseDto dto = mapper.convertValue(detail,SellerOrderDetailResponseDto.class);
                            dto.setProduct(mapper.convertValue(detail.getProductEntity(), ProductForBuyerDTO.class));
                return dto;
                }
                )
                .toList();

        for (SellerOrderDetailResponseDto detail : details){
            if(detail.getProduct()!= null){
                soldProductCount +=detail.getQuantity();
                totalRevenue += (detail.getProduct().getPrice() * detail.getQuantity());
            }
        }


        AdminStaticsDto statics = new AdminStaticsDto(
                allUsersCount,
                allOrdersCount,
                soldProductCount,
                totalRevenue
        );

        return statics;
    }

    @Override
    public SellerStaticsDto getSellerStatics(Long sellerID) {
        List<SellerOrderResponseDto> sellerOrders = orderService.getSellerOrdersOnSellerId(sellerID);
        long allOrdersCount = sellerOrders.size();
        long soldProductCount = 0;
        double totalRevenue = 0;

        for (SellerOrderResponseDto order : sellerOrders){
            totalRevenue += order.getOrderAmount();
            for(SellerOrderDetailResponseDto detail : order.getSellerOrderDetailsDto()){
                soldProductCount += detail.getQuantity();
            }
        }

        return new SellerStaticsDto(
                allOrdersCount,
                soldProductCount,
                totalRevenue
        );
    }

    @Override
    public BuyerStaticsDto getBuyerStatics(Long buyerId) {
        long allOrdersCount=0;
        long boughtProductCount=0;
        double totalSpent=0.0;
        List<OrderResponseDto> ordersByBuyer =  orderService.getOrdersByBuyerId(buyerId);

        allOrdersCount = ordersByBuyer.size();

        for(OrderResponseDto order : ordersByBuyer){
            List<OrderDetailResponseDto> details =  order.getOrderDetails();
            for(OrderDetailResponseDto detail : details){
                boughtProductCount += detail.getQuantity();
                totalSpent += (detail.getProduct().getPrice() *detail.getQuantity());
            }
        }

        return new BuyerStaticsDto(allOrdersCount,
                boughtProductCount,
                totalSpent);
    }

    @Override
    public boolean checkExistByUsername(String username) {
        if (username != null) {
            return userRepository.findByUsername(username)
                    .isPresent(
                    );
        }
        return false;
    }


    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while encrypting password", e);
        }
    }

    private String saveImage(String username, MultipartFile image) {
        // Extract the file extension from the image
        String fileExtension = extractFileExtension(Objects.requireNonNull(image.getOriginalFilename()));

        // Define the image path and file name
        Path filePath = imagePath.resolve(username +"."+ fileExtension);

        try {
            // Save the image file to the specified path
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the saved image file name (username.extension)
            return filePath.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error saving image for user " + username, e);
        }
    }


}