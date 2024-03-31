package dev.peter.flightbooking.repository;

import dev.peter.flightbooking.model.Flight;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    @Query(value = "SELECT * FROM flight WHERE CAST(start_date AS DATE) = CAST(?1 AS DATE) AND CAST(end_date AS DATE) = CAST(?2 AS DATE);", nativeQuery = true)
    List<Flight> findByStartDateAndEndDate(String startDate, String endDate);
    List<Flight> findByEndLocation(String endLocation);
    List<Flight> findByStartLocation(String startLocation);
}
