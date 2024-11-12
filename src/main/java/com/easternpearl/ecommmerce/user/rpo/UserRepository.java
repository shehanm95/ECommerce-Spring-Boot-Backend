package com.easternpearl.ecommmerce.user.rpo;

import com.easternpearl.ecommmerce.user.entity.UserEntity;
import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findByUserRole(UserRole role);
}

