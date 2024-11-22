package com.easternpearl.ecommmerce.user.service;

import com.easternpearl.ecommmerce.user.DAO.LoginDAO;
import com.easternpearl.ecommmerce.user.DAO.RegisterDAO;
import com.easternpearl.ecommmerce.user.DTO.AdminStaticsDto;
import com.easternpearl.ecommmerce.user.DTO.BuyerStaticsDto;
import com.easternpearl.ecommmerce.user.DTO.SellerStaticsDto;
import com.easternpearl.ecommmerce.user.DTO.UserDTO;
import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> findAll();
    Optional<UserDTO> findByUsername(String username);
    UserDTO login(LoginDAO loginDAO);
    UserDTO register(RegisterDAO registerDAO, MultipartFile image);
    List<UserDTO> findByRole(UserRole role);
    Optional<UserDTO> findById(Integer id);
    void deleteUser(int id);

    AdminStaticsDto getAdminStatics();

    SellerStaticsDto getSellerStatics(Integer sellerID);

    BuyerStaticsDto getBuyerStatics(Integer buyerId);
}
