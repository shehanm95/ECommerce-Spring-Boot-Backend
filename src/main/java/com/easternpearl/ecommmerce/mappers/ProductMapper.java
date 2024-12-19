package com.easternpearl.ecommmerce.mappers;

import com.easternpearl.ecommmerce.dto.ProductDTO;
import com.easternpearl.ecommmerce.entity.ProductEntity;

public interface ProductMapper {
    ProductEntity toEntity(ProductDTO productDTO);
    ProductDTO toDto (ProductEntity productDto);
}
