package com.easternpearl.ecommmerce.dto;


import com.easternpearl.ecommmerce.enums.UserRole;
import com.easternpearl.ecommmerce.enums.UserState;
import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private UserRole userRole;
    private UserState userState;
    private String imageLink;
}
