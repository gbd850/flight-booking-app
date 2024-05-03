package dev.peter.flightbooking.repository;

import dev.peter.flightbooking.model.Customer;
import dev.peter.flightbooking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByUsername(String username);

    @Query("select c.bookedFlights from Customer c where c.id = ?1")
    Set<Flight> findBookedFlightsByCustomerId(Integer id);
}
