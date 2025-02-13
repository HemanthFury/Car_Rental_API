package com.fury.car_rental_api;

import com.fury.car_rental_api.config.CustomJwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CustomJwtProperties.class)
public class CarRentalApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(CarRentalApiApplication.class, args);
	}

}
