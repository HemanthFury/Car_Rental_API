package com.fury.car_rental_api.service;

import com.fury.car_rental_api.entity.Car;
import com.fury.car_rental_api.entity.CarSpecifications;
import com.fury.car_rental_api.model.CarDTO;
import com.fury.car_rental_api.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CarServiceImpl implements CarService {
    @Autowired
    private CarRepository carRepository;

    @Override
    public ResponseEntity<?> addCar(CarDTO carDTO) {
        Car car = convertToEntity(carDTO);
        Car savedCar = carRepository.save(car);
        CarDTO result = convertToDTO(savedCar);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public ResponseEntity<?> updateCar(Long carId, CarDTO carDTO) {
        Car car = carRepository.findById(carId).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found with id: " + carId);
        }

        updateCarEntity(car, carDTO);

        Car updatedCar = carRepository.save(car);
        CarDTO result = convertToDTO(updatedCar);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> getAllCars(String brand, String model, String type, String availabilityStatus) {
        List<Car> cars;
        if (brand != null || model != null || type != null || availabilityStatus != null) {
            cars = carRepository.findAll(Specification
                    .where(CarSpecifications.hasBrand(brand))
                    .and(CarSpecifications.hasModel(model))
                    .and(CarSpecifications.hasType(type))
                    .and(CarSpecifications.hasAvailabilityStatus(availabilityStatus))
            );
        } else {
            cars = carRepository.findAll();
        }

        List<CarDTO> carDTOs = cars.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(carDTOs);
    }


    private Car convertToEntity(CarDTO carDTO) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setManufacturingDate(carDTO.getManufacturingDate());
        car.setType(carDTO.getType());
        car.setRentalPricePerDay(carDTO.getRentalPricePerDay());
        car.setAvailabilityStatus(carDTO.getAvailabilityStatus());
        return car;
    }


    private CarDTO convertToDTO(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setId(car.getId());
        carDTO.setBrand(car.getBrand());
        carDTO.setModel(car.getModel());
        carDTO.setManufacturingDate(car.getManufacturingDate());
        carDTO.setType(car.getType());
        carDTO.setRentalPricePerDay(car.getRentalPricePerDay());
        carDTO.setAvailabilityStatus(car.getAvailabilityStatus());
        return carDTO;
    }


    private void updateCarEntity(Car car, CarDTO carDTO) {
        car.setRentalPricePerDay(carDTO.getRentalPricePerDay());
        car.setAvailabilityStatus(carDTO.getAvailabilityStatus());
    }
}


