package com.easternpearl.ecommmerce.dto;

import com.easternpearl.ecommmerce.enums.ProductState;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    @NotNull(message = "product name cannot be empty or null")
    @NotEmpty(message = "product name cannot be empty or null")
    private String productName;
    @NotEmpty(message = "product price cannot be empty")
    private Double price;
    @NotEmpty(message = "product category cannot be empty")
    private Integer category;
    @NotEmpty(message = "product category cannot be empty")
    private Integer subCategory;
    private String productImageLink;
    private UserDTO sellerDto;
    private Double rate = 0.0;
    private Integer rateCount = 0;
    private ProductState productState;
    private Integer productCount;
    private String productCode;
    private Boolean isNew = true;
}
