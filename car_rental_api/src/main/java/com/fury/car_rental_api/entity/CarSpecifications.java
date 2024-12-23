package com.fury.car_rental_api.entity;

import org.springframework.data.jpa.domain.Specification;


public class CarSpecifications {

    public static Specification<Car> hasBrand(String brand) {
        return (root, query, criteriaBuilder) ->
                brand == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("brand"), brand);
    }

    public static Specification<Car> hasModel(String model) {
        return (root, query, criteriaBuilder) ->
                model == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("model"), model);
    }

    public static Specification<Car> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Car> hasAvailabilityStatus(String availabilityStatus) {
        return (root, query, criteriaBuilder) ->
                availabilityStatus == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("availabilityStatus"), availabilityStatus);
    }
}

