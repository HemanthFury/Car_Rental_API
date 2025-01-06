package com.fury.car_rental_api.service;

import com.fury.car_rental_api.entity.Booking;
import com.fury.car_rental_api.entity.Car;
import com.fury.car_rental_api.entity.User;
import com.fury.car_rental_api.model.BookingDTO;
import com.fury.car_rental_api.repository.BookingRepository;
import com.fury.car_rental_api.repository.CarRepository;
import com.fury.car_rental_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Override
    public ResponseEntity<?> createBooking(BookingDTO bookingDTO) {
        log.info("Creating booking for user ID: {}", bookingDTO.getUserId());
        if (!bookingDTO.getStartDate().isBefore(bookingDTO.getEndDate()) && !bookingDTO.getStartDate().isEqual(bookingDTO.getEndDate())) {
            log.error("Booking creation failed: Start date must be less than or equal to end date");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Start date must be less than or equal to end date\"}");
        }

        User user = userRepository.findById(bookingDTO.getUserId()).orElse(null);
        if (user == null) {
            log.error("Booking creation failed: User not found with ID: {}", bookingDTO.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"User not found with id: " + bookingDTO.getUserId() + "\"}");
        }

        Car car = carRepository.findById(bookingDTO.getCarId()).orElse(null);
        if (car == null) {
            log.error("Booking creation failed: Car not found with ID: {}", bookingDTO.getCarId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Car not found with id: " + bookingDTO.getCarId() + "\"}");
        }

        if ("Unavailable".equals(car.getAvailabilityStatus())) {
            log.error("Booking creation failed: Car with ID {} is unavailable", bookingDTO.getCarId());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Car is not available\"}");
        }

        double totalAmount = calculateTotalAmount(bookingDTO.getStartDate(), bookingDTO.getEndDate(), car.getRentalPricePerDay());

        Booking booking = convertToEntity(bookingDTO);
        booking.setUser(user);
        booking.setCar(car);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("CONFIRMED");
        booking.setUserName(user.getName());
        Booking savedBooking = bookingRepository.save(booking);

        car.setAvailabilityStatus("Unavailable");
        carRepository.save(car);

        BookingDTO result = convertToDTO(savedBooking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        String responseMessage = String.format("{\"message\": \"Booking created successfully\", \"bookingId\": %d, \"totalAmount\": %.2f}", savedBooking.getId(), totalAmount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage);
    }

    @Override
    public ResponseEntity<?> cancelBooking(Long bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            log.error("Booking cancellation failed: Booking not found with ID: {}", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"Booking not found with id: " + bookingId + "\"}");
        }

        Car car = booking.getCar();
        bookingRepository.delete(booking);

        car.setAvailabilityStatus("Available");
        carRepository.save(car);
        log.info("Booking cancelled successfully with ID: {}", bookingId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\": \"Booking canceled successfully\"}");
    }

    @Override
    public ResponseEntity<?> getAllBookingsForUser(Long userId) {
        log.info("Fetching all bookings for user with ID: {}", userId);
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            log.error("no bookings done by the user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("{\"message\": \"No bookings found for user with id: " + userId + "\"}");
        }

        List<BookingDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingDTO bookingDTO = convertToDTO(booking);
            bookingDTOs.add(bookingDTO);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(bookingDTOs);
    }

    // Helper methods
    private double calculateTotalAmount(LocalDate startDate, LocalDate endDate, double rentalPricePerDay) {
        return ChronoUnit.DAYS.between(startDate, endDate) * rentalPricePerDay;
    }

    private Booking convertToEntity(BookingDTO bookingDTO) {
        log.debug("Converting BookingDTO to Booking entity");
        Booking booking = new Booking();
        booking.setStartDate(bookingDTO.getStartDate());
        booking.setEndDate(bookingDTO.getEndDate());
        return booking;
    }

    private BookingDTO convertToDTO(Booking booking) {
        log.debug("Converting Booking entity to BookingDTO");
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(booking.getId());
        bookingDTO.setUserId(booking.getUser().getId());
        bookingDTO.setCarId(booking.getCar().getId());
        bookingDTO.setStartDate(booking.getStartDate());
        bookingDTO.setEndDate(booking.getEndDate());
        bookingDTO.setTotalAmount(booking.getTotalAmount());
        bookingDTO.setStatus(booking.getStatus());
        return bookingDTO;
    }
}
