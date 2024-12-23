package com.fury.car_rental_api.controller;

import com.fury.car_rental_api.model.UserDTO;
import com.fury.car_rental_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        ResponseEntity<?> response = userService.registerUser(userDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        return response;
    }


    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        ResponseEntity<?> response = userService.getUserById(userId);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof UserDTO) {
            return ResponseEntity.ok(response.getBody());
        }
        return response;
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}

