package com.easternpearl.ecommmerce.service.impl;

import com.easternpearl.ecommmerce.repo.CategoryRepository;
import com.easternpearl.ecommmerce.dto.SubCategoryRequest;
import com.easternpearl.ecommmerce.entity.SubCategory;
import com.easternpearl.ecommmerce.repo.SubCategoryRepository;
import com.easternpearl.ecommmerce.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public SubCategory saveSubCategory(SubCategoryRequest subCategory) {
        SubCategory sub = new SubCategory(0L, subCategory.subCategoryName(), subCategory.categoryId());

        return subCategoryRepository.save(sub);
    }

    @Override
    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }

    @Override
    public SubCategory getSubCategoryById(Long id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
    }

    @Override
    public void deleteSubCategoryById(Long id) {
        subCategoryRepository.deleteById(id);
    }

    @Override
    public List<SubCategory> getSubCategoriesByCategory(Long categoryId) {
        return subCategoryRepository.findAllByCategoryId(categoryId);
    }

    public List<SubCategory> saveAll(List<SubCategory> subCategories) {
        return subCategoryRepository.saveAll(subCategories);
    }
}