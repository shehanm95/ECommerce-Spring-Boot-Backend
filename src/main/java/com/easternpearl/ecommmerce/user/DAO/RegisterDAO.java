package com.easternpearl.ecommmerce.user.DAO;

import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDAO {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private String password;
    private LocalDate birthdate;
    private UserRole userRole;
}