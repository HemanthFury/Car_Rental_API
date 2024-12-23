package com.fury.car_rental_api.service;

import com.fury.car_rental_api.model.BookingDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {
    ResponseEntity<?> createBooking(BookingDTO bookingDTO);
    ResponseEntity<?> cancelBooking(Long bookingId);
    ResponseEntity<?> getAllBookingsForUser(Long userId);
}


