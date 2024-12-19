package com.easternpearl.ecommmerce.entity;

import com.easternpearl.ecommmerce.enums.UserRole;
import com.easternpearl.ecommmerce.enums.UserState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<ProductEntity> products;
}

