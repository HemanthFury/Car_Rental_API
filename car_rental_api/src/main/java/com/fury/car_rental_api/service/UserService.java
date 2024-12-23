package com.fury.car_rental_api.service;

import com.fury.car_rental_api.model.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> registerUser(UserDTO userDTO);
    ResponseEntity<?> getUserById(Long userId);
}


