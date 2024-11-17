package com.easternpearl.ecommmerce.product;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.easternpearl.ecommmerce.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

   private final ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAllForCustomers() {
        return productRepository.findAll();
    }

    public List<Product> findByCategory(Integer category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }


    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }
}

