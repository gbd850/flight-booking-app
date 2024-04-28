package dev.peter.flightbooking.repository;

import dev.peter.flightbooking.model.Customer;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FlightRepository flightRepository;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void givenValidUsername_whenFindByUsername_thenReturnCustomer() {
        // given
        String username = "username";
        Customer customer = new Customer(null, username, "", Role.USER, new HashSet<>());

        customerRepository.save(customer);

        Customer expected = new Customer(1, username, "", Role.USER, new HashSet<>());

        // when
        Optional<Customer> actual = customerRepository.findByUsername(username);

        // then
        assertThat(actual)
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("username", username);
    }

    @Test
    void givenInvalidUsername_whenFindByUsername_thenReturnEmptyOptional() {
        // given
        String username = "username";

        // when
        Optional<Customer> actual = customerRepository.findByUsername(username);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void givenNull_whenFindByUsername_thenReturnEmptyOptional() {
        // given
        String username = null;

        // when
        Optional<Customer> actual = customerRepository.findByUsername(username);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void givenValidId_whenFindBookedFlightsByCustomerId_thenReturnFlightsSet() {
        // given
        Flight flight = new Flight(null, "flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), null, "start", null, true);

        Integer id = 1;
        Customer customer = new Customer(id, "username", "", Role.USER, Set.of(flight));

        customerRepository.save(customer);

        // when
        Set<Flight> bookedFlights = customerRepository.findBookedFlightsByCustomerId(id);

        // then
        assertThat(bookedFlights)
                .hasSize(1)
                .allMatch(fl -> fl.getName().equals(flight.getName()))
                .allMatch(fl -> Objects.equals(fl.getPrice(), flight.getPrice()))
                .allMatch(fl -> fl.getStartDate().equals(flight.getStartDate()))
                .allMatch(fl ->fl.getStartLocation().equals(flight.getStartLocation()))
                .allMatch(fl ->fl.isAvailable() == flight.isAvailable());
    }

    @Test
    void givenInvalidId_whenFindBookedFlightsByCustomerId_thenReturnEmptySet() {
        // given
        Integer id = 1;

        // when
        Set<Flight> bookedFlights = customerRepository.findBookedFlightsByCustomerId(id);

        // then
        assertThat(bookedFlights).isEmpty();
    }
}