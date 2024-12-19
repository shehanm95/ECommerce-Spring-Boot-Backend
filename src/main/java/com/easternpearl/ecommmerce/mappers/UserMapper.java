package com.easternpearl.ecommmerce.mappers;

import com.easternpearl.ecommmerce.dto.UserDTO;
import com.easternpearl.ecommmerce.entity.UserEntity;

public interface UserMapper {
    UserEntity toEntity (UserDTO userDTO);
    UserDTO toDto (UserEntity userEntity);
}
