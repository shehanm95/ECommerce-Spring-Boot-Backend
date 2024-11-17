package com.easternpearl.ecommmerce.subCategory.DAO;

import com.easternpearl.ecommmerce.category.Category;

public record SubCategoryRequest(
String subCategoryName, Long categoryId) { }
