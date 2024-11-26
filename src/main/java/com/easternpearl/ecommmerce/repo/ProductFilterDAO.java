package com.easternpearl.ecommmerce.repo;


import com.easternpearl.ecommmerce.entity.ProductEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductFilterDAO {

    private final EntityManager em;

    public List<ProductEntity> findByFilterObj(
            String text,
            Integer category,
            Integer subCategory
    ) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder
                .createQuery(ProductEntity.class);

        // SELECT * FROM Product;
        Root<ProductEntity> root = criteriaQuery.from(ProductEntity.class);

        // Prepare predicates (WHERE conditions)
        Predicate predicate = criteriaBuilder.conjunction(); // Start with a "true" predicate

        // Add text filter if text is not empty
        if (text != null && !text.trim().isEmpty()) {
            Predicate productNamePredicate = criteriaBuilder.like(
                    root.get("productName"), "%" + text + "%"
            );
            predicate = criteriaBuilder.and(predicate, productNamePredicate);
        }

        // Add category filter if category is not 0
        if (category != null && category != 0) {
            Predicate categoryPredicate = criteriaBuilder.equal(
                    root.get("category"), category
            );
            predicate = criteriaBuilder.and(predicate, categoryPredicate);
        }

        // Add sub-category filter if subCategory is not 0
        if (subCategory != null && subCategory != 0) {
            Predicate subCategoryPredicate = criteriaBuilder.equal(
                    root.get("subCategory"), subCategory
            );
            predicate = criteriaBuilder.and(predicate, subCategoryPredicate);
        }

        // Set the WHERE clause
        criteriaQuery.where(predicate);

        // Execute query and fetch results
        return em.createQuery(criteriaQuery).getResultList();
    }

}