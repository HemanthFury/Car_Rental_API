package com.fury.car_rental_api.service;

import com.fury.car_rental_api.entity.User;
import com.fury.car_rental_api.model.UserDTO;
import com.fury.car_rental_api.model.UserResponseDTO;
import com.fury.car_rental_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> registerUser(UserDTO userDTO) {
        log.info("Attempting to register user with email: {}", userDTO.getEmail());
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            log.error("Registration failed: Email {} is already in use", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Email is already in use\"}");
        }

        if (userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            log.error("Registration failed: Phone number {} is already in use", userDTO.getPhone());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Phone number is already in use\"}");
        }

        User user = convertToEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        UserResponseDTO result = convertToResponseDTO(savedUser);
        log.info("User registered successfully with email: {}", userDTO.getEmail());
        String responseMessage = String.format("{\"message\": \"User registered successfully\", \"userId\": %d}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage);
    }

    @Override
    public ResponseEntity<?> getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"User not found with id: " + userId + "\"}");
        }

        UserResponseDTO userResponseDTO = convertToResponseDTO(user);
        log.info("User fetched successfully with ID: {}", userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(userResponseDTO);
    }

    // Helper methods
    private User convertToEntity(UserDTO userDTO) {
        log.debug("Converting UserDTO to User entity");
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        log.debug("Converting User entity to UserResponseDTO");
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setPhone(user.getPhone());
        userResponseDTO.setCreatedAt(user.getCreatedAt());
        return userResponseDTO;
    }

}




