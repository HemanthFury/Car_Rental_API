package com.fury.car_rental_api.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CarDTO {
    private Long id;
    @NotNull(message = "brand is required")
    private String brand;
    @NotNull(message = "model is required")
    private String model;
    @NotNull(message = "manufacturingDate is required")
    private LocalDate manufacturingDate;
    @NotNull(message = "Type of the car is required")
    private String type;
    @NotNull(message = "rentalPricePerDay is required")
    private double rentalPricePerDay;
    @NotNull(message = "availabilityStatus is required")
    private String availabilityStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

}

