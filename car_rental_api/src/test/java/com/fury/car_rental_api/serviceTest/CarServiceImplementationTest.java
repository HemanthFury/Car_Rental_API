package com.fury.car_rental_api.serviceTest;

import com.fury.car_rental_api.entity.Car;
import com.fury.car_rental_api.model.CarDTO;
import com.fury.car_rental_api.repository.CarRepository;
import com.fury.car_rental_api.service.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CarServiceImplementationTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCar_Success() {
        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Toyota");

        Car car = new Car();
        car.setBrand("Toyota");
        when(carRepository.save(any(Car.class))).thenReturn(car);

        ResponseEntity<?> response = carService.addCar(carDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    public void testUpdateCar_Success() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Toyota");

        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Honda");

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        ResponseEntity<?> response = carService.updateCar(1L, carDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    public void testUpdateCar_NotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        CarDTO carDTO = new CarDTO();
        carDTO.setBrand("Honda");

        ResponseEntity<?> response = carService.updateCar(1L, carDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(carRepository, never()).save(any(Car.class));
    }


    @Test
    public void testGetAllCars_ByParameters_Success() {
        Car car = new Car();
        car.setBrand("Toyota");

        List<Car> cars = List.of(car);
        when(carRepository.findAll(any(Specification.class))).thenReturn(cars);

        ResponseEntity<?> response = carService.getAllCars("Toyota", null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(((List<?>) response.getBody()).isEmpty());
        verify(carRepository).findAll(any(Specification.class));
    }
}
