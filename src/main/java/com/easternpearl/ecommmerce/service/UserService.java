package com.easternpearl.ecommmerce.service;

import com.easternpearl.ecommmerce.dto.LoginDto;
import com.easternpearl.ecommmerce.dto.RegisterDto;
import com.easternpearl.ecommmerce.dto.AdminStaticsDto;
import com.easternpearl.ecommmerce.dto.BuyerStaticsDto;
import com.easternpearl.ecommmerce.dto.SellerStaticsDto;
import com.easternpearl.ecommmerce.dto.UserDTO;
import com.easternpearl.ecommmerce.enums.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> findAll();
    Optional<UserDTO> findByUsername(String username);
    UserDTO login(LoginDto loginDto);
    UserDTO register(RegisterDto registerDto, MultipartFile image);
    List<UserDTO> findByRole(UserRole role);
    Optional<UserDTO> findById(Long id);
    void deleteUser(Long id);

    AdminStaticsDto getAdminStatics();

    SellerStaticsDto getSellerStatics(Long sellerID);

    BuyerStaticsDto getBuyerStatics(Long buyerId);

    boolean checkExistByUsername(String username);
}
