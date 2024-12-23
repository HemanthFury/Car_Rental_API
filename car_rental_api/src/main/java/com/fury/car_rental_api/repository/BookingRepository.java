package com.fury.car_rental_api.repository;

import com.fury.car_rental_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCarIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long carId, LocalDate startDate, LocalDate endDate);
}

