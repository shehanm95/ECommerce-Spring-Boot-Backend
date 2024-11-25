package com.easternpearl.ecommmerce.category.repo;

import com.easternpearl.ecommmerce.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
