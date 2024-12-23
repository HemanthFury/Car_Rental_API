package com.fury.car_rental_api.service;

import com.fury.car_rental_api.entity.Booking;
import com.fury.car_rental_api.entity.Car;
import com.fury.car_rental_api.entity.User;
import com.fury.car_rental_api.model.BookingDTO;
import com.fury.car_rental_api.repository.BookingRepository;
import com.fury.car_rental_api.repository.CarRepository;
import com.fury.car_rental_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
        if (!bookingDTO.getStartDate().isBefore(bookingDTO.getEndDate()) && !bookingDTO.getStartDate().isEqual(bookingDTO.getEndDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Start date must be less than or equal to end date");
        }

        User user = userRepository.findById(bookingDTO.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with id: " + bookingDTO.getUserId());
        }

        Car car = carRepository.findById(bookingDTO.getCarId()).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Car not found with id: " + bookingDTO.getCarId());
        }

        if ("Unavailable".equals(car.getAvailabilityStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Car is not available ");
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

        String responseMessage = String.format("{\"message\": \"Booking created successfully\", \"bookingId\": %d, \"totalAmount\": %.2f}", savedBooking.getId(), totalAmount);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Booking not found with id: " + bookingId);
        }

        Car car = booking.getCar();
        bookingRepository.delete(booking);

        car.setAvailabilityStatus("Available");
        carRepository.save(car);
        return ResponseEntity.ok("Booking canceled successfully");
    }

    @Override
    public ResponseEntity<?> getAllBookingsForUser(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        if (bookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No bookings found for user with id: " + userId);
        }

        List<BookingDTO> bookingDTOs = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingDTO bookingDTO = convertToDTO(booking);
            bookingDTOs.add(bookingDTO);
        }

        return ResponseEntity.ok(bookingDTOs);
    }


    private double calculateTotalAmount(LocalDate startDate, LocalDate endDate, double rentalPricePerDay) {
        return ChronoUnit.DAYS.between(startDate, endDate) * rentalPricePerDay;
    }


    private Booking convertToEntity(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setStartDate(bookingDTO.getStartDate());
        booking.setEndDate(bookingDTO.getEndDate());
        return booking;
    }


    private BookingDTO convertToDTO(Booking booking) {
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
