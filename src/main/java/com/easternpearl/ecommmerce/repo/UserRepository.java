package com.easternpearl.ecommmerce.repo;

import com.easternpearl.ecommmerce.entity.UserEntity;
import com.easternpearl.ecommmerce.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findByUserRole(UserRole role);

}

