package com.easternpearl.ecommmerce.user.entity;

import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import com.easternpearl.ecommmerce.user.entity.enums.UserState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    @Column(unique = true,nullable = false)
    private String username;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserState userState = UserState.ACTIVE;  // default ACTIVE

    private String password;
    private String phone;
    private LocalDate birthdate;
    private LocalDate createdDate = LocalDate.now();
    private LocalDate modifiedDate;
    private String imageLink;
}

