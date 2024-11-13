package com.easternpearl.ecommmerce.subCategory;

import com.easternpearl.ecommmerce.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subCategoryName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
