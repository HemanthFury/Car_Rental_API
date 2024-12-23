package com.fury.car_rental_api.controller;

import com.fury.car_rental_api.model.CarDTO;
import com.fury.car_rental_api.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cars")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/register")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDTO carDTO) {
        ResponseEntity<?> response = carService.addCar(carDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Car added successfully. Car ID: " + ((CarDTO) response.getBody()).getId());
        }
        return response;
    }


    @PutMapping("/update/{carId}")
    public ResponseEntity<?> updateCar(@PathVariable Long carId, @Valid @RequestBody CarDTO carDTO) {
        ResponseEntity<?> response = carService.updateCar(carId, carDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode())
                    .body("Car updated successfully. Car ID: " + ((CarDTO) response.getBody()).getId());
        }
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<?> getAllCars( @RequestParam(required = false) String brand, @RequestParam(required = false) String model, @RequestParam(required = false) String type, @RequestParam(required = false) String availabilityStatus) {
        ResponseEntity<?> response = carService.getAllCars(brand, model, type, availabilityStatus);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }
        return response;
    }
}
