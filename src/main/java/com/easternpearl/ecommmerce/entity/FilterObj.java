package com.easternpearl.ecommmerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterObj {
    private String text;
    private int category;
    private int subCategory;
}
