package com.easternpearl.ecommmerce.category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category addCategory(Category category);
    void removeCategory(Long id);
    Category editCategory(Long id, Category updatedCategory);
    Optional<Category> getCategoryById(Long id);
    List<Category> findAllCategories();
    List<Category> saveAll(List<Category> categories);
}
