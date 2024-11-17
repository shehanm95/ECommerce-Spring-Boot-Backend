package com.easternpearl.ecommmerce.subCategory;

import com.easternpearl.ecommmerce.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findAllByCategoryId(Long categoryId);
}

