package com.fury.car_rental_api.controller;

import com.fury.car_rental_api.model.BookingDTO;
import com.fury.car_rental_api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@Validated
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookCar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        log.info("Creating booking for user ID: {}", bookingDTO.getUserId());
        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Booking created successfully for user ID: {}", bookingDTO.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to create booking for user ID: {}", bookingDTO.getUserId());
        return response;
    }

    @DeleteMapping("/delete/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);
        ResponseEntity<?> response = bookingService.cancelBooking(bookingId);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Booking cancelled successfully with ID: {}", bookingId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Booking canceled successfully\"}");
        }
        log.error("Failed to cancel booking with ID: {}", bookingId);
        return response;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllBookingsForUser(@PathVariable Long userId) {
        log.info("Fetching all bookings for user with ID: {}", userId);
        ResponseEntity<?> response = bookingService.getAllBookingsForUser(userId);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Fetched all bookings successfully for user with ID: {}", userId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to fetch bookings for user with ID: {}", userId);
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

