package com.easternpearl.ecommmerce.category.controller;

import com.easternpearl.ecommmerce.category.service.CategoryService;
import com.easternpearl.ecommmerce.category.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Add a new category
    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        System.out.println(category);
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(savedCategory);
    }

    // Remove a category by ID
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeCategory(@PathVariable Long id) {
        categoryService.removeCategory(id);
        return ResponseEntity.ok("Category removed successfully");
    }

    // Edit a category by ID
    @PutMapping("/edit/{id}")
    public ResponseEntity<Category> editCategory(@PathVariable Long id, @RequestBody Category updatedCategory) {
        Category category = categoryService.editCategory(id, updatedCategory);
        return ResponseEntity.ok(category);
    }

    // Get a category by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all categories
    @GetMapping("/all")
    public ResponseEntity<List<Category>> findAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        return ResponseEntity.ok(categories);
    }


    @PostMapping("/bulk")
    public ResponseEntity<List<Category>> saveCategories(@RequestBody List<Category> categories) {
        List<Category> savedCategories = categoryService.saveAll(categories);
        return ResponseEntity.ok(savedCategories);
    }
}
