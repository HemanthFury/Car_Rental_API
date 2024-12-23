package com.fury.car_rental_api.service;

import com.fury.car_rental_api.entity.User;
import com.fury.car_rental_api.model.UserDTO;
import com.fury.car_rental_api.model.UserResponseDTO;
import com.fury.car_rental_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }

        if (userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number is already in use");
        }

        User user = convertToEntity(userDTO);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        UserResponseDTO result = convertToResponseDTO(savedUser);

        String responseMessage = String.format("{\"message\": \"User registered successfully\", \"userId\": %d}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> getUserById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + userId);
        }

        UserResponseDTO userResponseDTO = convertToResponseDTO(user);
        return ResponseEntity.ok(userResponseDTO);
    }

    // Private method for converting UserDTO to User entity
    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    // Private method for converting User entity to UserResponseDTO
    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setPhone(user.getPhone());
        userResponseDTO.setCreatedAt(user.getCreatedAt());
        return userResponseDTO;
    }
}




