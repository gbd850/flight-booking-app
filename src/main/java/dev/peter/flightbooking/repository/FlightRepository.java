package dev.peter.flightbooking.repository;

import dev.peter.flightbooking.model.Flight;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    @Query(value = "SELECT * FROM flight WHERE DATE(start_date) = DATE(?1) AND DATE(end_date) = DATE(?2);", nativeQuery = true)
    List<Flight> findByStartDateAndEndDate(String startDate, String endDate);
    List<Flight> findByEndLocation(String endLocation);
    List<Flight> findByStartLocation(String startLocation);
}
