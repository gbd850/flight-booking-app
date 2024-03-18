package dev.peter.flightbooking.service;

import dev.peter.flightbooking.dto.FlightRequestDto;
import dev.peter.flightbooking.model.Flight;
import dev.peter.flightbooking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    private List<Flight> filterAvailableFlights(List<Flight> flights) {
        return flights.stream()
                .filter(Flight::isAvailable).toList();
    }

    public List<Flight> getFLightsByStartLocation(String startLocation, boolean filterUnavailable) {

        List<Flight> flights = Collections.unmodifiableList(flightRepository.findByStartLocation(startLocation));

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find flights matching start location");
        }

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights;
    }

    public List<Flight> getFLightsByEndLocation(String endLocation, boolean filterUnavailable) {

        List<Flight> flights = Collections.unmodifiableList(flightRepository.findByEndLocation(endLocation));

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find flights matching end location");
        }

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights;
    }

    public List<Flight> getFLightsByTimeFrame(String startDate, String endDate, boolean filterUnavailable) {

        List<Flight> flights = Collections.unmodifiableList(flightRepository.findByStartDateAndEndDate(startDate, endDate));

        if (flights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find flights matching time frame");
        }

        if (filterUnavailable) {
            flights = filterAvailableFlights(flights);
        }

        return flights;
    }

    public Flight createFlight(FlightRequestDto flightRequestDto) {

        Flight flight = new Flight(
                null,
                flightRequestDto.name(),
                flightRequestDto.price(),
                flightRequestDto.startDate(),
                flightRequestDto.endDate(),
                flightRequestDto.startLocation(),
                flightRequestDto.endLocation(),
                flightRequestDto.isAvailable()
        );

        try {
            flightRepository.save(flight);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flight;
    }

    public void deleteFlight(Integer id) {
        if (flightRepository.existsById(id)) {

            flightRepository.deleteById(id);

        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found");
    }

    public Flight editFlight(Integer id, FlightRequestDto flightRequestDto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));

        flight.updateEntityFromDto(flightRequestDto);

        flightRepository.save(flight);

        return flight;
    }
}
