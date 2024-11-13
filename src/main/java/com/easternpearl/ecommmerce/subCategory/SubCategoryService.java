package com.easternpearl.ecommmerce.subCategory;

import com.easternpearl.ecommmerce.category.Category;

import java.util.List;

public interface SubCategoryService {
    SubCategory saveSubCategory(SubCategory subCategory);
    List<SubCategory> getAllSubCategories();
    SubCategory getSubCategoryById(Long id);
    void deleteSubCategoryById(Long id);
    List<SubCategory> getSubCategoriesByCategory(Category category);
}
