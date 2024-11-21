package com.easternpearl.ecommmerce.product;


import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
import com.easternpearl.ecommmerce.user.DTO.SellerNameAndImg;
import com.easternpearl.ecommmerce.user.rpo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.easternpearl.ecommmerce.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    public final String MAIN_LINK = "http://localhost:8080";

   private final ProductRepository productRepository;
   private final ObjectMapper mapper;
   private final UserRepository userRepository;

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

    public List<ProductForBuyerDTO> getProductsForBuyers(){
        return  convertListForBuyers(productRepository.getNewProducts());
    }

    public List<ProductForBuyerDTO> convertListForBuyers(List<Product> products){
        List<ProductForBuyerDTO> buyerProductsList = products.stream()
                .map(p -> mapper.convertValue(p, ProductForBuyerDTO.class))
                .toList();

        for (int i = 0; i < buyerProductsList.size(); i++) {
            //only if buyer exist
            if(userRepository.existsById(buyerProductsList.get(i).getSellerId())) {
                ProductForBuyerDTO p =  buyerProductsList.get(i);
                SellerNameAndImg sellerDetails = userRepository.getSellerNameAndImgLink(p.getSellerId());
                sellerDetails.setImageLink( MAIN_LINK+ sellerDetails.getImageLink());
                p.setSellerDetails(sellerDetails);


            }
        }


        return buyerProductsList;
    }
}

