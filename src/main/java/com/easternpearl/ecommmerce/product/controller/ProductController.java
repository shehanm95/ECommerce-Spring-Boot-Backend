package com.easternpearl.ecommmerce.product.controller;

import com.easternpearl.ecommmerce.product.ProductService;
import com.easternpearl.ecommmerce.product.dto.ProductForBuyerDTO;
import com.easternpearl.ecommmerce.product.model.FilterObj;
import com.easternpearl.ecommmerce.product.model.ProductEntity;
import com.easternpearl.ecommmerce.product.model.enums.ProductState;
import com.easternpearl.ecommmerce.product.repo.ProductFilterDAO;
import com.easternpearl.ecommmerce.user.DTO.UserNameAndImg;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private static final String PRODUCT_IMAGES_DIR = "src/main/resources/products";


    private final ProductService productService;
    private final ProductFilterDAO productFilterDAO;


    private void createFolder(){
        File directory = new File(PRODUCT_IMAGES_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    @PostMapping("/add")
    public ResponseEntity<ProductEntity> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("category") int category,
            @RequestParam("subCategory") int subCategory,
            @RequestParam("sellerId") Integer sellerId,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("productCount") Integer productCount)
    {
        createFolder();
        ProductState productState = (productCount > 0) ? ProductState.InStock : ProductState.OutOfStock ;

        try {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductName(productName);
            productEntity.setPrice(price);
            productEntity.setCategory(category);
            productEntity.setSubCategory(subCategory);
            productEntity.setProductImageLink(imageFile.getOriginalFilename());
            productEntity.setSeller();
                    sellerId,
                    0.0,
                    0,
                    productState,
                    productCount,
                    "Product still code not added",
                    true,
                    null
            );

            ProductEntity savedProductEntity = productService.save(productEntity);
            String productCode = String.format("P%04dS%04d", savedProductEntity.getId(),sellerId);
            savedProductEntity.setProductCode(productCode);
            String imageLink = saveImageFile( savedProductEntity.getId(),imageFile);
            savedProductEntity.setProductImageLink(imageLink);
            savedProductEntity = productService.save(savedProductEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProductEntity);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            File imageFile = new File(PRODUCT_IMAGES_DIR + "/" + filename);
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(imageFile);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<ProductEntity> product = productService.findById(id);
        if (product.isPresent()) {
            deleteImageFile(product.get().getProductImageLink());
            productService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductForBuyerDTO>> getAllProductsForCustomers() {
        List<ProductEntity> productEntities = productService.findAllForCustomers();
        return ResponseEntity.ok(productService.convertListForBuyers(productEntities));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        Optional<ProductEntity> product = productService.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/get/all/{category}")
    public ResponseEntity<List<ProductEntity>> getProductsByCategory(@PathVariable Integer category) {
        List<ProductEntity> productEntities = productService.findByCategory(category);
        return ResponseEntity.ok(productEntities);
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<List<ProductEntity>> getProductsByName(@PathVariable String name) {
        List<ProductEntity> productEntities = productService.findByName(name);
        return ResponseEntity.ok(productEntities);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ProductEntity> editProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("category") int category,
            @RequestParam("subCategory") int subCategory,
            @RequestParam("imageFile") MultipartFile imageFile) {
        Optional<ProductEntity> existingProduct = productService.findById(id);
        if (existingProduct.isPresent()) {
            try {
                String imageFileName = saveImageFile(id, imageFile);
                ProductEntity updatedProductEntity = existingProduct.get();
                deleteImageFile(updatedProductEntity.getProductImageLink());
                updatedProductEntity.setProductName(productName);
                updatedProductEntity.setPrice(price);
                updatedProductEntity.setCategory(category);
                updatedProductEntity.setProductImageLink(imageFileName);
                productService.save(updatedProductEntity);
                return ResponseEntity.ok(updatedProductEntity);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Helper Methods

    private String saveImageFile(Long imageId , MultipartFile imageFile) throws IOException {
        System.out.println(imageFile.getOriginalFilename()+"oringal name");
        String[] nameParts = imageFile.getOriginalFilename().split("\\.");
        String fileName = "file-" + imageId +"."+ (nameParts[nameParts.length-1]);
        Path filePath = Paths.get(PRODUCT_IMAGES_DIR, fileName);
        Files.copy(imageFile.getInputStream(), filePath);
        return "http://localhost:8080/products/images/"+fileName;
    }

    private void deleteImageFile(String imageFileName) {
        try {
            Path filePath = Paths.get(PRODUCT_IMAGES_DIR, imageFileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/search/{text}/{category}/{subCategory}")
    public List<ProductEntity> getProductsOnFilterParams(
            @PathVariable(required = false) String text,
            @PathVariable int category,
            @PathVariable int subCategory

    ){
       return productFilterDAO.findByFilterObj(text,category,subCategory);
    }
 @PostMapping("/filter")
    public List<ProductEntity> getProductsOnFilterObj(
         @RequestBody FilterObj filterObj

         ){
       return productFilterDAO.findByFilterObj(filterObj.getText(),filterObj.getCategory(),filterObj.getSubCategory());
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ProductEntity>> saveProducts(@RequestBody List<ProductEntity> productEntities) {
        List<ProductEntity> savedProductEntities = productService.saveAll(productEntities);
        return ResponseEntity.ok(savedProductEntities);
    }


    @GetMapping("/forBuyer/all")
    public ResponseEntity<List<ProductForBuyerDTO>> getProductsForBuyers(){
        return ResponseEntity.ok(productService.getProductsForBuyers());
    }

    @GetMapping("/sellerDetails/{sellerId}")
    public UserNameAndImg getSellerDetails(@PathVariable Integer sellerId){
        return productService.getSellerNameAndImage(sellerId);
    }


}

