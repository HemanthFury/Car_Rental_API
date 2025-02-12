package com.fury.car_rental_api.serviceTest;
import com.fury.car_rental_api.entity.Booking;
import com.fury.car_rental_api.entity.Car;
import com.fury.car_rental_api.entity.User;
import com.fury.car_rental_api.model.BookingDTO;
import com.fury.car_rental_api.repository.BookingRepository;
import com.fury.car_rental_api.repository.CarRepository;
import com.fury.car_rental_api.repository.UserRepository;
import com.fury.car_rental_api.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceImplementationTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking_Success() {
        User user = new User();
        user.setId(1L);
        Car car = new Car();
        car.setId(1L);
        car.setAvailabilityStatus("Available");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setCar(car);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStartDate(LocalDate.now());
        bookingDTO.setEndDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(bookingRepository).save(any(Booking.class));
        verify(carRepository).save(any(Car.class));
    }

    @Test
    public void testCreateBooking_UserNotFound()  {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStartDate(LocalDate.now());
        bookingDTO.setEndDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"message\": \"User not found with id: 1\"}", response.getBody());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    public void testCreateBooking_CarNotFound() {
        User user = new User();
        user.setId(1L);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStartDate(LocalDate.now());
        bookingDTO.setEndDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"message\": \"Car not found with id: 1\"}", response.getBody());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateBooking_CarUnavailable() {
        User user = new User();
        user.setId(1L);
        Car car = new Car();
        car.setId(1L);
        car.setAvailabilityStatus("Unavailable");

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStartDate(LocalDate.now());
        bookingDTO.setEndDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"message\": \"Car is not available\"}", response.getBody());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateBooking_InvalidDates() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1L);
        bookingDTO.setCarId(1L);
        bookingDTO.setStartDate(LocalDate.now().plusDays(1));
        bookingDTO.setEndDate(LocalDate.now());

        ResponseEntity<?> response = bookingService.createBooking(bookingDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"message\": \"Start date must be less than or equal to end date\"}", response.getBody());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    public void testCancelBooking_Success() {
        Booking booking = new Booking();
        booking.setId(1L);
        Car car = new Car();
        booking.setCar(car);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ResponseEntity<?> response = bookingService.cancelBooking(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Booking canceled successfully\"}", response.getBody());
        verify(bookingRepository).delete(any(Booking.class));
        verify(carRepository).save(any(Car.class));
    }

    @Test
    public void testCancelBooking_NotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookingService.cancelBooking(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"message\": \"Booking not found with id: 1\"}", response.getBody());
        verify(bookingRepository, never()).delete(any(Booking.class));
    }

    @Test
    public void testGetAllBookingsForUser_Success() {
        User user = new User();
        user.setId(1L);
        Car car = new Car();
        car.setId(1L);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setId(1L);

        List<Booking> bookings = List.of(booking);

        when(bookingRepository.findByUserId(1L)).thenReturn(bookings);

        ResponseEntity<?> response = bookingService.getAllBookingsForUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(((List<BookingDTO>) response.getBody()).isEmpty());
        verify(bookingRepository).findByUserId(1L);
    }

    @Test
    public void testGetAllBookingsForUser_NoneFound() {
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findByUserId(1L)).thenReturn(bookings);

        ResponseEntity<?> response = bookingService.getAllBookingsForUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"message\": \"No bookings found for user with id: 1\"}", response.getBody());
        verify(bookingRepository).findByUserId(1L);
    }
}
