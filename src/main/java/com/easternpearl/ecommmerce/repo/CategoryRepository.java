package com.easternpearl.ecommmerce.repo;

import com.easternpearl.ecommmerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
