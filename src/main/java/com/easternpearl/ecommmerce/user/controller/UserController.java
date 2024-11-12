package com.easternpearl.ecommmerce.user.controller;

import com.easternpearl.ecommmerce.user.DAO.LoginDAO;
import com.easternpearl.ecommmerce.user.DAO.RegisterDAO;
import com.easternpearl.ecommmerce.user.DTO.UserDTO;
import com.easternpearl.ecommmerce.user.entity.enums.UserRole;
import com.easternpearl.ecommmerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
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
    public ResponseEntity<UserDTO> login(@RequestBody LoginDAO loginDAO) {
        UserDTO userDTO = userService.login(loginDAO);
        if(userDTO != null) return ResponseEntity.ok(userService.login(loginDAO));
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

        RegisterDAO registerDAO = new RegisterDAO();
        registerDAO.setFirstName(firstName);
        registerDAO.setLastName(lastName);
        registerDAO.setUsername(username);
        registerDAO.setEmail(email);
        registerDAO.setPassword(password);
        registerDAO.setBirthdate(LocalDate.parse(birthdate));
        registerDAO.setUserRole(userRole);


        System.out.println(new UserDTO());
        UserDTO user = userService.register(registerDAO, image);
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
    public UserDTO findById(@PathVariable Integer id) {
        return userService.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
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
            throw new RuntimeException("File not found: " + imageName);
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
}

