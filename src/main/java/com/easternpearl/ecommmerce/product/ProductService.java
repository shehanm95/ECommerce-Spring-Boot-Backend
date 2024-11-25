package com.easternpearl.ecommmerce.product;


import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
import com.easternpearl.ecommmerce.product.model.ProductEntity;
import com.easternpearl.ecommmerce.product.repo.ProductRepository;
import com.easternpearl.ecommmerce.user.DTO.UserNameAndImg;
import com.easternpearl.ecommmerce.user.rpo.UserRepository;
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

    public List<ProductForBuyerDTO> getProductsForBuyers(){
        return  convertListForBuyers(productRepository.getNewProducts());
    }

    public List<ProductForBuyerDTO> convertListForBuyers(List<ProductEntity> productEntities){
        List<ProductForBuyerDTO> buyerProductsList = productEntities.stream()
                .map(p -> mapper.convertValue(p, ProductForBuyerDTO.class))
                .toList();

        for (int i = 0; i < buyerProductsList.size(); i++) {
            addSellerDetailToProductForBuyerDto(buyerProductsList.get(i));
        }


        return buyerProductsList;
    }

    public ProductForBuyerDTO addSellerDetailToProductForBuyerDto(ProductForBuyerDTO product) {
        if(userRepository.existsById(product.getSellerId())){
            UserNameAndImg sellerDetails = getSellerNameAndImage(product.getSellerId());
            product.setSellerDetails(sellerDetails);
        }
        return product;
    }

    public UserNameAndImg getSellerNameAndImage(int sellerId) {
        if(userRepository.existsById(sellerId)){
            UserNameAndImg sellerDetails = userRepository.getSellerNameAndImgLink(sellerId);
            sellerDetails.setImageLink( MAIN_LINK+ sellerDetails.getImageLink());
            return sellerDetails;
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@ Seller Not Found @@@@@@@@@@@@@@@@@");
        return null;
    }

    public ProductForBuyerDTO getProductById(Long productId) {
        return mapper.convertValue(productRepository.findById(productId),ProductForBuyerDTO.class);
    }
}

