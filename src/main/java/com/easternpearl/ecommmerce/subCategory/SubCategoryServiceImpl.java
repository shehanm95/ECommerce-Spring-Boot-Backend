package com.easternpearl.ecommmerce.subCategory;

import com.easternpearl.ecommmerce.category.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;





@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    @Autowired
    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public SubCategory saveSubCategory(SubCategory subCategory) {
        return subCategoryRepository.save(subCategory);
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
    public List<SubCategory> getSubCategoriesByCategory(Category category) {
        return subCategoryRepository.findAllByCategory(category);
    }
}
