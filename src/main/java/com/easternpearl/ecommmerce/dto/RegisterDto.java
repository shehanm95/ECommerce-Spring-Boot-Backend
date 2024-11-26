package com.easternpearl.ecommmerce.dto;

import com.easternpearl.ecommmerce.enums.UserRole;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDto {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private String password;
    private LocalDate birthdate;
    private UserRole userRole;
}