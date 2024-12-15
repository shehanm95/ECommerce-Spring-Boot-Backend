package com.easternpearl.ecommmerce.controller;

import com.easternpearl.ecommmerce.dto.LoginDto;
import com.easternpearl.ecommmerce.dto.RegisterDto;
import com.easternpearl.ecommmerce.dto.AdminStaticsDto;
import com.easternpearl.ecommmerce.dto.BuyerStaticsDto;
import com.easternpearl.ecommmerce.dto.SellerStaticsDto;
import com.easternpearl.ecommmerce.dto.UserDTO;
import com.easternpearl.ecommmerce.entity.UserEntity;
import com.easternpearl.ecommmerce.enums.UserRole;
import com.easternpearl.ecommmerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
//    http://localhost:8080/swagger-ui/index.html#/
    private final Path imagePath = Paths.get("src/main/resources/images/user");

    private final UserService userService;

    @GetMapping("/all")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{username}")
    public UserDTO findByUsername(@PathVariable String username) {
        return userService.findByUsername(username).orElse(null);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDto loginDto) {
        UserDTO userDTO = userService.login(loginDto);
        if(userDTO != null) return ResponseEntity.ok(userService.login(loginDto));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("birthdate") String birthdate,
            @RequestParam("userRole") UserRole userRole,
            @RequestParam("image") MultipartFile image) {

        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName(firstName);
        registerDto.setLastName(lastName);
        registerDto.setUsername(username);
        registerDto.setEmail(email);
        registerDto.setPassword(password);
        registerDto.setBirthdate(LocalDate.parse(birthdate));
        registerDto.setUserRole(userRole);


        System.out.println(new UserDTO());
        UserDTO user = userService.register(registerDto, image);
        System.out.println("saved user : "+user);

        if(user != null){
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/isUserExist/{username}")
    public boolean checkExistByUsername(@PathVariable String username){
        boolean exist =  userService.checkExistByUsername(username);
        System.out.println(exist +" ===============================exist");
        return exist;
    }

    @PostMapping("/register/admin")
    public ResponseEntity<UserDTO> registerAsAdmin(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("birthdate") String birthdate,
            @RequestParam("adminPassword") String adminPassword,
            @RequestParam("image") MultipartFile image) {


        System.out.println(adminPassword +"==========admin===============");
        if(!adminPassword.equals("Ecom123")){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);}
        System.out.println(adminPassword);
        RegisterDto registerDto = new RegisterDto();
        registerDto.setFirstName(firstName);
        registerDto.setLastName(lastName);
        registerDto.setUsername(username);
        registerDto.setEmail(email);
        registerDto.setPassword(password);
        registerDto.setBirthdate(LocalDate.parse(birthdate));
        registerDto.setUserRole(UserRole.ADMIN);


        System.out.println(new UserDTO());
        UserDTO user = userService.register(registerDto, image);
        System.out.println("saved user : "+user);

        if(user != null){
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @GetMapping("/role/{role}")
    public List<UserDTO> findByRole(@PathVariable UserRole role) {
        return userService.findByRole(role);
    }


    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable Long id) {
        return userService.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/image/{imageName}")
    public ResponseEntity<FileSystemResource> getImageFile(@PathVariable String imageName) {
        // Construct the full image path using the base imagePath and the image name
        Path filePath = imagePath.resolve(imageName);

        // Check if the file exists
        if (Files.exists(filePath)) {
            FileSystemResource file = new FileSystemResource(filePath.toFile());

            // Get the content type based on the file extension
            MediaType mediaType = getMediaTypeForImage(imageName);

            // Return the file with proper headers for download or display
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + imageName)
                    .contentType(mediaType)  // Set the correct media type based on file extension
                    .body(file);
        } else {
            System.out.println("File not found===============: " + imageName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    // Helper method to determine the media type based on the file extension
    private MediaType getMediaTypeForImage(@NotNull String imageName) {
        if (imageName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (imageName.endsWith(".jpeg") || imageName.endsWith(".jpg")) {
            return MediaType.IMAGE_JPEG;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM; // Default type for unsupported formats
        }
    }

    @GetMapping("/adminStatics")
    public AdminStaticsDto getSellerStatics(){
        return userService.getAdminStatics();
    }
    @GetMapping("/sellerStatics/{sellerId}")
    public SellerStaticsDto getSellerStatics(@PathVariable Long sellerId){
        return userService.getSellerStatics(sellerId);
    }
    @GetMapping("/buyerStatics/{buyerId}")
    public BuyerStaticsDto getBuyerStatics(@PathVariable Long buyerId){
        return userService.getBuyerStatics(buyerId);
    }

    @PostMapping("/bulk")
    public List<UserEntity> saveUserBulk(@RequestBody List<UserEntity> users){
        return userService.saveAll(users);
    }
}

