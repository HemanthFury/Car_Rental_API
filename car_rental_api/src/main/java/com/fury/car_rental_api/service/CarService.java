package com.fury.car_rental_api.service;

import com.fury.car_rental_api.model.CarDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CarService {
    ResponseEntity<?> addCar(CarDTO carDTO);
    ResponseEntity<?> updateCar(Long carId, CarDTO carDTO);;
    ResponseEntity<?> getAllCars(String brand, String model, String type, String availabilityStatus);
}


