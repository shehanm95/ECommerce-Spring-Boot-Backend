package com.easternpearl.ecommmerce.controller;

import com.easternpearl.ecommmerce.mappers.ProductMapper;
import com.easternpearl.ecommmerce.mappers.UserMapper;
import com.easternpearl.ecommmerce.service.ProductService;
import com.easternpearl.ecommmerce.dto.ProductDTO;
import com.easternpearl.ecommmerce.entity.FilterObj;
import com.easternpearl.ecommmerce.entity.ProductEntity;
import com.easternpearl.ecommmerce.enums.ProductState;
import com.easternpearl.ecommmerce.repo.ProductFilterDAO;
import com.easternpearl.ecommmerce.entity.UserEntity;
import com.easternpearl.ecommmerce.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private static final String PRODUCT_IMAGES_DIR = "src/main/resources/products";


    private final ProductService productService;
    private final ProductFilterDAO productFilterDAO;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final UserMapper userMapper;
    private final ProductMapper productMapper;


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
            @RequestParam("sellerId") Long sellerId,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("productCount") Integer productCount)
    {
        // Ensure the folder is created before the image is saved
        createFolder();

        // Determine the product state based on stock count
        ProductState productState = (productCount > 0) ? ProductState.InStock : ProductState.OutOfStock;

        try {
            // Create the product entity and set its properties
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProductName(productName);
            productEntity.setPrice(price);
            productEntity.setCategory(category);
            productEntity.setSubCategory(subCategory);
            productEntity.setProductImageLink(imageFile.getOriginalFilename()); // Set the image file name temporarily

            // Handle the seller lookup and set the seller
            Optional<UserEntity> seller = userRepository.findById(sellerId);
            if (seller.isPresent()) {
                productEntity.setSeller(seller.get());
            } else {
                // Seller not found - return a 404 response
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null); // Alternatively, you could return a meaningful message
            }

            // Set other product properties
            productEntity.setProductState(productState);
            productEntity.setProductCount(productCount);
            productEntity.setProductCode("still not added");
            productEntity.setIsNew(true); // Assuming new products are always marked true on creation
            productEntity.setRate(0.0); // Default rating
            productEntity.setRateCount(0); // Default rating count
            productEntity.setProductImageLink(imageFile.getOriginalFilename());
            productEntity.setId(0L);
            productEntity.setSellerOrderDetail(Collections.emptyList());
            // Log the details for debugging purposes
            //logger.info("Creating product: {}", productEntity);

            // Save the product entity to the database
            ProductEntity savedProductEntity = productService.save(productEntity);
            // Generate the product code after saving the entity
            String productCode = String.format("P%04dS%04d", savedProductEntity.getId(), sellerId);
            savedProductEntity.setProductCode(productCode);

            // Save the image file and get the link
            String imageLink = saveImageFile(savedProductEntity.getId(), imageFile);
            savedProductEntity.setProductImageLink(imageLink);

            // Save the updated product entity with the generated product code and image link
            savedProductEntity = productService.save(savedProductEntity);

            // Return the response with status CREATED
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProductEntity);

        } catch (IOException e) {
            // Log the exception and return a server error response
            logger.error("Error while saving product: {}", e.getMessage());
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
    public List<ProductDTO> getAllProductsForCustomers() {
        return productService.findAllForCustomers().stream().map(productMapper::toDto).toList();
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

//    @GetMapping("/search/{text}/{category}/{subCategory}")
//    public List<ProductEntity> getProductsOnFilterParams(
//            @PathVariable(required = false) String text,
//            @PathVariable int category,
//            @PathVariable int subCategory
//
//    ){
//       return productFilterDAO.findByFilterObj(text,category,subCategory);
//    }
 @PostMapping("/filter")
    public List<ProductDTO> getProductsOnFilterObj(
         @RequestBody FilterObj filterObj){
        List<ProductEntity> productEntities =  productFilterDAO.findByFilterObj(filterObj.getText(),filterObj.getCategory(),filterObj.getSubCategory());
       return productEntities.stream().map(productMapper::toDto).toList();

    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ProductEntity>> saveProducts(@RequestBody List<ProductEntity> productEntities) {
        List<ProductEntity> savedProductEntities = productService.saveAll(productEntities);
        return ResponseEntity.ok(savedProductEntities);
    }


    @GetMapping("/forBuyer/all")
    public List<ProductDTO> getProductsForBuyers(){
        return productService.findAllForCustomers().stream().map(productMapper::toDto).toList();
    }


}

