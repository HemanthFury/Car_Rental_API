package com.fury.car_rental_api.serviceTest;

import com.fury.car_rental_api.entity.User;
import com.fury.car_rental_api.model.UserDTO;
import com.fury.car_rental_api.repository.UserRepository;
import com.fury.car_rental_api.service.UserServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImplementation userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());

        User user = new User();
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userService.registerUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.registerUser(userDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }
}

