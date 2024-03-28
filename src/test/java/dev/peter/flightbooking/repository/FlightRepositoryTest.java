package dev.peter.flightbooking.repository;

import dev.peter.flightbooking.model.Flight;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    @AfterEach
    void tearDown() {
        flightRepository.deleteAll();
    }

    @Test
    void givenValidStartDateAndEndDate_whenFindByStartDateAndEndDate_thenReturnFlightsList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-10-11";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByStartDateAndEndDate(startDate, endDate);
        // then
        assertThat(flights).isNotEmpty()
                .hasSize(1)
                .allMatch(el ->
                        el.getStartDate().toLocalDateTime().toLocalDate().toString().equals(startDate)
                                && el.getEndDate().toLocalDateTime().toLocalDate().toString().equals(endDate)
                                && el.getName().equals("Flight1"));
    }

    @Test
    void givenValidStartDateAndEndDate_whenFindByStartDateAndEndDate_thenReturnEmptyList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-10-11";
        // when
        List<Flight> flights = flightRepository.findByStartDateAndEndDate(startDate, endDate);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenInvalidStartDateAndValidEndDate_whenFindByStartDateAndEndDate_thenReturnEmptyList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-10-11";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByStartDateAndEndDate(null, endDate);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenValidStartDateAndInvalidEndDate_whenFindByStartDateAndEndDate_thenReturnEmptyList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-10-11";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByStartDateAndEndDate(startDate, null);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenInvalidStartDateAndInvalidEndDate_whenFindByStartDateAndEndDate_thenReturnEmptyList() {
        // given
        String startDate = "2011-10-10";
        String endDate = "2011-10-11";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf(startDate + " 00:00:00"), Timestamp.valueOf(endDate + " 00:00:00"), "Location1", null, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByStartDateAndEndDate(null, null);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenValidEndLocation_whenFindByEndLocation_thenReturnFlightsList() {
        // given
        String endLocation = "Location2";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), null, "Location1", endLocation, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByEndLocation(endLocation);
        // then
        assertThat(flights).isNotEmpty()
                .hasSize(1)
                .allMatch(el ->
                        el.getEndLocation().equals(endLocation)
                                && el.getName().equals("Flight1"));
    }

    @Test
    void givenValidEndLocation_whenFindByEndLocation_thenReturnEmptyList() {
        // given
        String endLocation = "Location2";
        // when
        List<Flight> flights = flightRepository.findByEndLocation(endLocation);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenInvalidEndLocation_whenFindByEndLocation_thenReturnEmptyList() {
        // given
        String endLocation = "Location2";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), null, "Location1", endLocation, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByEndLocation(null);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenValidStartLocation_whenFindByStartLocation_thenReturnFlightsList() {
        // given
        String startLocation = "Location1";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), null, startLocation, null, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByStartLocation(startLocation);
        // then
        assertThat(flights).isNotEmpty()
                .hasSize(1)
                .allMatch(el ->
                        el.getStartLocation().equals(startLocation)
                                && el.getName().equals("Flight1"));
    }

    @Test
    void givenValidStartLocation_whenFindByStartLocation_thenReturnEmptyList() {
        // given
        String startLocation = "Location1";
        // when
        List<Flight> flights = flightRepository.findByStartLocation(startLocation);
        // then
        assertThat(flights).isEmpty();
    }

    @Test
    void givenInvalidStartLocation_whenFindByStartLocation_thenReturnEmptyList() {
        // given
        String startLocation = "Location1";
        Flight flight = new Flight(null, "Flight1", 10.5, Timestamp.valueOf("2011-10-10 00:00:00"), null, startLocation, null, true);
        flightRepository.save(flight);
        // when
        List<Flight> flights = flightRepository.findByStartLocation(null);
        // then
        assertThat(flights).isEmpty();
    }
}