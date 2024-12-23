package com.fury.car_rental_api.repository;

import com.fury.car_rental_api.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {
}


