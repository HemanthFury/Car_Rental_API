package com.fury.car_rental_api.controller;

import com.fury.car_rental_api.model.CarDTO;
import com.fury.car_rental_api.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/v1/cars")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/register")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDTO carDTO) {
        log.info("Registering car with brand: {}", carDTO.getBrand());
        ResponseEntity<?> response = carService.addCar(carDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Car registered successfully: {}", carDTO.getBrand());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to register car: {}", carDTO.getBrand());
        return response;
    }


    @PutMapping("/update/{carId}")
    public ResponseEntity<?> updateCar(@PathVariable Long carId, @Valid @RequestBody CarDTO carDTO) {
        log.info("Updating car with ID: {}", carId);
        ResponseEntity<?> response = carService.updateCar(carId, carDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Car updated successfully with ID: {}", carId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to update car with ID: {}", carId);
        return response;
    }


    @GetMapping("/search")
    public ResponseEntity<?> getAllCars(@RequestParam(required = false) String brand,
                                        @RequestParam(required = false) String model,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(required = false) String availabilityStatus) {
        log.info("Searching cars with parameters - brand: {}, model: {}, type: {}, availabilityStatus: {}", brand, model, type, availabilityStatus);
        ResponseEntity<?> response = carService.getAllCars(brand, model, type, availabilityStatus);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Cars search successful with parameters");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(response.getBody());
        }
        log.error("Failed to search cars with parameters");
        return response;
    }

}
