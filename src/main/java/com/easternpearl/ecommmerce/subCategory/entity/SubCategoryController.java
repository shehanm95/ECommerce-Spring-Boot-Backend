package com.easternpearl.ecommmerce.subCategory.entity;

import com.easternpearl.ecommmerce.category.repo.CategoryRepository;
import com.easternpearl.ecommmerce.subCategory.DAO.SubCategoryRequest;
import com.easternpearl.ecommmerce.subCategory.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/subcategories")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public SubCategoryController(SubCategoryService subCategoryService, CategoryRepository categoryRepository) {
        this.subCategoryService = subCategoryService;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<SubCategory> createSubCategory(@RequestBody SubCategoryRequest subCategory) {
        SubCategory createdSubCategory = subCategoryService.saveSubCategory(subCategory);
        return ResponseEntity.ok(createdSubCategory);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        List<SubCategory> subCategories = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getSubCategoryById(@PathVariable Long id) {
        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(subCategory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint to get subcategories by category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SubCategory>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesByCategory(categoryId);
        return ResponseEntity.ok(subCategories);
    }


    @PostMapping("/bulk")
    public ResponseEntity<List<SubCategory>> saveSubCategories(@RequestBody List<SubCategory> subCategories) {
        List<SubCategory> savedSubCategories = subCategoryService.saveAll(subCategories);
        return ResponseEntity.ok(savedSubCategories);
    }
}

