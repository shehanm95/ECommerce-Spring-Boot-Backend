package com.easternpearl.ecommmerce.service;


import com.easternpearl.ecommmerce.dto.ProductDTO;
import com.easternpearl.ecommmerce.entity.ProductEntity;
import com.easternpearl.ecommmerce.repo.ProductRepository;
import com.easternpearl.ecommmerce.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    public final String MAIN_LINK = "http://localhost:8080";

   private final ProductRepository productRepository;
   private final ObjectMapper mapper;
   private final UserRepository userRepository;



    public ProductEntity save(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<ProductEntity> findAllForCustomers() {
        return productRepository.findAll();
    }

    public List<ProductEntity> findByCategory(Integer category) {
        return productRepository.findByCategory(category);
    }

    public List<ProductEntity> findByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }


    public List<ProductEntity> saveAll(List<ProductEntity> productEntities) {
        return productRepository.saveAll(productEntities);
    }

    public ProductDTO getProductById(Long productId) {
        return mapper.convertValue(productRepository.findById(productId), ProductDTO.class);
    }
}

