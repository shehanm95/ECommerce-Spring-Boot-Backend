package com.easternpearl.ecommmerce.mappers.impl;

import com.easternpearl.ecommmerce.dto.ProductDTO;
import com.easternpearl.ecommmerce.entity.ProductEntity;
import com.easternpearl.ecommmerce.mappers.ProductMapper;
import com.easternpearl.ecommmerce.mappers.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {
    private final ObjectMapper objectMapper;

    @Autowired
    @Lazy
    private UserMapper userMapper;

    @Override
    public ProductEntity toEntity(ProductDTO productDTO) {
        ProductEntity entity = objectMapper.convertValue(productDTO, ProductEntity.class);

        return entity;
    }

    @Override
    public ProductDTO toDto(ProductEntity productEntity) {
       ProductDTO dto = objectMapper.convertValue(productEntity, ProductDTO.class);
       dto.setSellerDto(userMapper.toDto(productEntity.getSeller()));
       return dto;
    }
}
