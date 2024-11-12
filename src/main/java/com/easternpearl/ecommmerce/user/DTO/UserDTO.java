package com.easternpearl.ecommmerce.user.DTO;


import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import com.easternpearl.ecommmerce.user.entity.enums.UserState;
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
