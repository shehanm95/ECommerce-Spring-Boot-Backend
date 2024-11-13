package com.easternpearl.ecommmerce.subCategory;

import com.easternpearl.ecommmerce.category.Category;
import com.easternpearl.ecommmerce.category.CategoryRepository;
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

    @PostMapping
    public ResponseEntity<SubCategory> createSubCategory(@RequestBody SubCategory subCategory) {
        SubCategory createdSubCategory = subCategoryService.saveSubCategory(subCategory);
        return ResponseEntity.ok(createdSubCategory);
    }

    @GetMapping
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        List<SubCategory> subCategories = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getSubCategoryById(@PathVariable Long id) {
        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(subCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategoryById(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint to get subcategories by category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SubCategory>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesByCategory(category);
        return ResponseEntity.ok(subCategories);
    }
}

