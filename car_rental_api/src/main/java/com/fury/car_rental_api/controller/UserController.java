package com.fury.car_rental_api.controller;

import com.fury.car_rental_api.model.UserDTO;
import com.fury.car_rental_api.model.UserResponseDTO;
import com.fury.car_rental_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Registering user with email: {}", userDTO.getEmail());
        ResponseEntity<?> response = userService.registerUser(userDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("User registered successfully: {}", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to register user: {}", userDTO.getEmail());
        return response;
    }


    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        log.info("Fetching user profile with ID: {}", userId);
        ResponseEntity<?> response = userService.getUserById(userId);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof UserResponseDTO) {
            log.info("User profile fetched successfully with ID: {}", userId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to fetch user profile with ID: {}", userId);
        return response;
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("Validation error on field {}: {}", fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) .body(errors);
    }
}

