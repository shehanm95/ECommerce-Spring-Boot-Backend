package com.easternpearl.ecommmerce.subCategory;

import com.easternpearl.ecommmerce.subCategory.DAO.SubCategoryRequest;

import java.util.List;

public interface SubCategoryService {
    SubCategory saveSubCategory(SubCategoryRequest subCategory);
    List<SubCategory> getAllSubCategories();
    SubCategory getSubCategoryById(Long id);
    void deleteSubCategoryById(Long id);
    List<SubCategory> getSubCategoriesByCategory(Long categoryId);
    public List<SubCategory> saveAll(List<SubCategory> subCategories);
}
