package com.fury.car_rental_api.controller;

import com.fury.car_rental_api.model.BookingDTO;
import com.fury.car_rental_api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@Validated
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookCar")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        return response;
    }


    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        ResponseEntity<?> response = bookingService.cancelBooking(bookingId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Booking canceled successfully");
        }
        return response;
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllBookingsForUser(@PathVariable Long userId) {
        ResponseEntity<?> response = bookingService.getAllBookingsForUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
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

