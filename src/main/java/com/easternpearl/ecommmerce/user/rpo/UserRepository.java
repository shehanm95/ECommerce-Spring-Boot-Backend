package com.easternpearl.ecommmerce.user.rpo;

import com.easternpearl.ecommmerce.user.DTO.UserNameAndImg;
import com.easternpearl.ecommmerce.user.entity.UserEntity;
import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findByUserRole(UserRole role);


    @Query("SELECT new com.easternpearl.ecommmerce.user.DTO.UserNameAndImg" +
            "(u.username, u.imageLink) " +
            "FROM UserEntity u WHERE u.id = :id")
    UserNameAndImg getSellerNameAndImgLink(@Param("id") Integer id);

}

