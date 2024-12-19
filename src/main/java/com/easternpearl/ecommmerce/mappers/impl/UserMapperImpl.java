package com.easternpearl.ecommmerce.mappers.impl;

import com.easternpearl.ecommmerce.dto.UserDTO;
import com.easternpearl.ecommmerce.entity.UserEntity;
import com.easternpearl.ecommmerce.mappers.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final ObjectMapper objectMapper;


    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserDTO toDto(UserEntity userEntity) {
        UserDTO dto = objectMapper.convertValue(userEntity,UserDTO.class);
        return dto;
    }
}
